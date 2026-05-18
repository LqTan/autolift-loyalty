"""ML Job Worker - polls ml_jobs table and executes pipelines."""
import argparse
import os
import sys
import time
from datetime import datetime
from pathlib import Path
from typing import Optional

import psycopg2
import psycopg2.extras

sys.path.insert(0, str(Path(__file__).parent.parent))

from autolift_ml.pipeline.build_features import build_features
from autolift_ml.pipeline.train import train_t_learner, predict_uplift
from autolift_ml.pipeline.evaluate import evaluate_model
from autolift_ml.pipeline.export import export_uplift_scores, export_feature_snapshots
from autolift_ml.gp.build_gp_input import build_gp_input
from autolift_ml.gp.train_gp_rules import train_gp_rules
from autolift_ml.gp.export_gp_rules import export_gp_rules


class MlJobWorker:
    def __init__(self, db_url: str, poll_interval: int = 10):
        self.db_url = db_url
        self.poll_interval = poll_interval
        self._conn = None

    def _get_conn(self):
        if self._conn is None or self._conn.closed:
            self._conn = psycopg2.connect(self.db_url)
        return self._conn

    def _close_conn(self):
        if self._conn and not self._conn.closed:
            self._conn.close()
            self._conn = None

    def run(self):
        print("=" * 60)
        print("ML Job Worker Started")
        print("=" * 60)
        print(f"Poll interval: {self.poll_interval}s")
        print(f"Database: {self.db_url.split('@')[1] if '@' in self.db_url else 'unknown'}")
        print("=" * 60)

        while True:
            try:
                self._process_pending_jobs()
            except Exception as e:
                print(f"Error in worker loop: {e}")
            time.sleep(self.poll_interval)

    def _process_pending_jobs(self):
        conn = self._get_conn()
        with conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor) as cur:
            cur.execute("""
                SELECT id, job_type, campaign_id, input_params, uplift_score_job_id
                FROM ml.ml_jobs
                WHERE status = 'PENDING'
                ORDER BY created_at ASC
                LIMIT 1
                FOR UPDATE SKIP LOCKED
            """)
            job = cur.fetchone()

        if not job:
            return

        job_id = job['id']
        job_type = job['job_type']
        campaign_id = job['campaign_id']
        input_params = job['input_params'] or {}
        uplift_score_job_id = job['uplift_score_job_id']

        print(f"\nProcessing job: {job_id} (type={job_type}, campaign={campaign_id})")

        with conn.cursor() as cur:
            cur.execute(
                "UPDATE ml.ml_jobs SET status = 'RUNNING', started_at = %s WHERE id = %s",
                (datetime.now(), job_id)
            )
            conn.commit()

        try:
            if job_type == 'UPLIFT_SCORING':
                result_path = self._run_uplift_pipeline(campaign_id, input_params)
            elif job_type == 'GP_RULE_EXTRACTION':
                result_path = self._run_gp_pipeline(campaign_id, input_params, uplift_score_job_id, conn)
            else:
                raise ValueError(f"Unknown job type: {job_type}")

            with conn.cursor() as cur:
                cur.execute(
                    "UPDATE ml.ml_jobs SET status = 'COMPLETED', result_path = %s, completed_at = %s WHERE id = %s",
                    (result_path, datetime.now(), job_id)
                )
                conn.commit()
            print(f"Job {job_id} COMPLETED: {result_path}")

        except Exception as e:
            import traceback
            error_msg = f"{type(e).__name__}: {str(e)}"
            print(f"Job {job_id} FAILED: {error_msg}")
            print(traceback.format_exc())
            with conn.cursor() as cur:
                cur.execute(
                    "UPDATE ml.ml_jobs SET status = 'FAILED', error_message = %s, completed_at = %s WHERE id = %s",
                    (error_msg, datetime.now(), job_id)
                )
                conn.commit()

    def _run_uplift_pipeline(self, campaign_id: str, input_params: dict) -> str:
        print(f"Running UPLIFT_SCORING pipeline for campaign: {campaign_id}")

        ml_dir = Path(__file__).parent.parent
        data_dir = ml_dir / "data"
        output_dir = ml_dir / "artifacts" / "outputs"
        model_dir = ml_dir / "artifacts" / "models"

        output_dir.mkdir(parents=True, exist_ok=True)
        model_dir.mkdir(parents=True, exist_ok=True)

        top_k_rate = input_params.get('top_k_rate', 0.20)
        model_version = input_params.get('model_version', 'v1')
        chunksize = input_params.get('chunksize', 300_000)

        train_features, test_features, _ = build_features(
            base_dir=data_dir,
            train_file="uplift_train.csv.gz",
            clients_file="clients.csv.gz",
            purchases_file="purchases.csv.gz",
            output_dir=output_dir,
            chunksize=chunksize,
        )

        feature_cols = [c for c in train_features.columns if c not in ["customer_id", "treatment_flg", "target"]]
        model_info = train_t_learner(
            train_features=train_features,
            model_dir=model_dir,
            base_model=input_params.get('base_model', 'RandomForestClassifier'),
            n_estimators=input_params.get('n_estimators', 100),
            random_state=input_params.get('random_state', 42),
        )

        test_df = test_features.copy()
        uplift_csv = export_uplift_scores(
            test_features=test_df,
            model_info=model_info,
            model_version=model_version,
            output_dir=output_dir,
            campaign_id=campaign_id,
        )

        feature_csv = export_feature_snapshots(
            test_features=test_df,
            model_version=model_version,
            output_dir=output_dir,
            campaign_id=campaign_id,
        )

        return str(uplift_csv)

    def _run_gp_pipeline(self, campaign_id: str, input_params: dict, uplift_score_job_id: Optional[str], conn) -> str:
        print(f"Running GP_RULE_EXTRACTION pipeline for campaign: {campaign_id}")

        if uplift_score_job_id:
            with conn.cursor() as cur:
                cur.execute("SELECT result_path FROM ml.ml_jobs WHERE id = %s", (uplift_score_job_id,))
                row = cur.fetchone()
                if row and row[0]:
                    uplift_scores_path = row[0]
                else:
                    uplift_scores_path = str(Path(__file__).parent.parent / "artifacts" / "outputs" / "customer_uplift_scores.csv")
        else:
            uplift_scores_path = str(Path(__file__).parent.parent / "artifacts" / "outputs" / "customer_uplift_scores.csv")

        feature_snapshots_path = str(Path(__file__).parent.parent / "artifacts" / "outputs" / "customer_feature_snapshots.csv")
        output_dir = Path(__file__).parent.parent / "artifacts" / "outputs"

        top_k_rate = input_params.get('top_k_rate', 0.20)
        model_version = input_params.get('model_version', 'v1')
        population_size = input_params.get('population_size', 200)
        generations = input_params.get('generations', 20)

        gp_input_path = output_dir / "gp_input.csv"
        build_gp_input(
            uplift_scores_path=Path(uplift_scores_path),
            feature_snapshots_path=Path(feature_snapshots_path),
            output_path=gp_input_path,
            top_k_rate=top_k_rate,
        )

        gp_df, rules = train_gp_rules(
            gp_input_path=gp_input_path,
            output_dir=output_dir,
            population_size=population_size,
            generations=generations,
        )

        gp_rules_path = output_dir / "gp_rules.csv"
        export_gp_rules(
            rules=rules,
            campaign_id=campaign_id,
            model_version=model_version,
            output_path=gp_rules_path,
            source_file="gp_rules.csv",
        )

        return str(gp_rules_path)


def main():
    parser = argparse.ArgumentParser(description="ML Job Worker - polls and executes ML jobs")
    parser.add_argument(
        "--db-url", "-d",
        type=str,
        default=os.environ.get("DATABASE_URL"),
        help="PostgreSQL connection URL (default: DATABASE_URL env var)"
    )
    parser.add_argument(
        "--poll-interval", "-p",
        type=int,
        default=10,
        help="Poll interval in seconds (default: 10)"
    )

    args = parser.parse_args()

    if not args.db_url:
        print("Error: --db-url required or set DATABASE_URL env var")
        print("Example: export DATABASE_URL=postgresql://postgres:postgres@localhost:5432/autolift_db")
        sys.exit(1)

    worker = MlJobWorker(db_url=args.db_url, poll_interval=args.poll_interval)
    try:
        worker.run()
    except KeyboardInterrupt:
        print("\nWorker stopped.")
        worker._close_conn()


if __name__ == "__main__":
    main()