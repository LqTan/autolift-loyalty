"""ML Job Worker - polls ml_jobs table and executes pipelines."""
import argparse
import json
import os
import sys
import time
from datetime import datetime
from pathlib import Path
from typing import Optional

import numpy as np
import pandas as pd
import psycopg2
import psycopg2.extras

sys.path.insert(0, str(Path(__file__).parent.parent))

from autolift_ml.pipeline.build_features import build_features
from autolift_ml.pipeline.train import train_t_learner, predict_uplift
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

    def _update_progress(self, conn, job_id: str, progress: int, message: str):
        with conn.cursor() as cur:
            cur.execute(
                "UPDATE ml.ml_jobs SET progress = %s, message = %s WHERE id = %s",
                (progress, message, job_id)
            )
            conn.commit()

    def _update_metrics(self, conn, job_id: str, metrics: dict):
        metrics_json = json.dumps(metrics)
        with conn.cursor() as cur:
            cur.execute(
                "UPDATE ml.ml_jobs SET metrics = %s WHERE id = %s",
                (metrics_json, job_id)
            )
            conn.commit()

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

        ts = datetime.now().strftime('%H:%M:%S')
        if not job:
            print(f"[{ts}] No PENDING job found, polling again in {self.poll_interval}s...", flush=True)
            return

        job_id = job['id']
        job_type = job['job_type']
        campaign_id = job['campaign_id']
        input_params = job['input_params'] or {}
        uplift_score_job_id = job['uplift_score_job_id']

        print(f"\n[{datetime.now().strftime('%H:%M:%S')}] Processing job: {job_id} (type={job_type}, campaign={campaign_id})")

        with conn.cursor() as cur:
            cur.execute(
                "UPDATE ml.ml_jobs SET status = 'RUNNING', started_at = %s WHERE id = %s",
                (datetime.now(), job_id)
            )
            conn.commit()
        print(f"[{datetime.now().strftime('%H:%M:%S')}] Marked job RUNNING")

        try:
            if job_type == 'UPLIFT_SCORING':
                result_path = self._run_uplift_pipeline(conn, job_id, campaign_id, input_params)
            elif job_type == 'GP_RULE_EXTRACTION':
                result_path = self._run_gp_pipeline(conn, job_id, campaign_id, input_params, uplift_score_job_id)
            else:
                raise ValueError(f"Unknown job type: {job_type}")

            with conn.cursor() as cur:
                cur.execute(
                    "UPDATE ml.ml_jobs SET status = 'COMPLETED', result_path = %s, completed_at = %s, progress = 100, message = 'Completed' WHERE id = %s",
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
                    "UPDATE ml.ml_jobs SET status = 'FAILED', error_message = %s, completed_at = %s, progress = 0, message = 'Failed' WHERE id = %s",
                    (error_msg, datetime.now(), job_id)
                )
                conn.commit()

    def _run_uplift_pipeline(self, conn, job_id, campaign_id: str, input_params: dict) -> str:
        print(f"[{datetime.now().strftime('%H:%M:%S')}] START UPLIFT_SCORING pipeline for campaign: {campaign_id}", flush=True)

        ml_dir = Path(__file__).parent.parent
        data_dir = ml_dir / "data"
        output_dir = ml_dir / "artifacts" / "outputs"
        model_dir = ml_dir / "artifacts" / "models"

        output_dir.mkdir(parents=True, exist_ok=True)
        model_dir.mkdir(parents=True, exist_ok=True)

        top_k_rate = input_params.get('top_k_rate', 0.20)
        model_version = input_params.get('model_version', 'v1')
        chunksize = input_params.get('chunksize', 300_000)

        print(f"[{datetime.now().strftime('%H:%M:%S')}] STEP 1/4: Building features...", flush=True)
        train_features, test_features, _ = build_features(
            base_dir=data_dir,
            train_file="uplift_train.csv.gz",
            clients_file="clients.csv.gz",
            purchases_file="purchases.csv.gz",
            output_dir=output_dir,
            chunksize=chunksize,
        )
        print(f"[{datetime.now().strftime('%H:%M:%S')}] STEP 1/4: Done. Train: {len(train_features)}, Test: {len(test_features)}", flush=True)
        self._update_progress(conn, job_id, 25, "Training model...")

        print(f"[{datetime.now().strftime('%H:%M:%S')}] STEP 2/4: Training model...", flush=True)
        feature_cols = [c for c in train_features.columns if c not in ["customer_id", "treatment_flg", "target"]]
        model_info = train_t_learner(
            train_features=train_features,
            model_dir=model_dir,
            base_model=input_params.get('base_model', 'RandomForestClassifier'),
            n_estimators=input_params.get('n_estimators', 100),
            random_state=input_params.get('random_state', 42),
        )
        print(f"[{datetime.now().strftime('%H:%M:%S')}] STEP 2/4: Done. Models saved to {model_dir}", flush=True)
        self._update_progress(conn, job_id, 65, "Exporting scores...")

        print(f"[{datetime.now().strftime('%H:%M:%S')}] STEP 3/4: Exporting uplift scores...", flush=True)
        test_df = test_features.copy()
        uplift_csv = export_uplift_scores(
            test_features=test_df,
            model_info=model_info,
            model_version=model_version,
            output_dir=output_dir,
            campaign_id=campaign_id,
        )
        print(f"[{datetime.now().strftime('%H:%M:%S')}] STEP 3/4: Done. Output: {uplift_csv}", flush=True)
        self._update_progress(conn, job_id, 85, "Computing metrics...")

        print(f"[{datetime.now().strftime('%H:%M:%S')}] STEP 4/4: Computing and saving metrics...", flush=True)
        metrics = self._compute_metrics(train_features, feature_cols, model_info, top_k_rate)
        self._update_metrics(conn, job_id, metrics)

        feature_csv = export_feature_snapshots(
            test_features=test_df,
            model_version=model_version,
            output_dir=output_dir,
            campaign_id=campaign_id,
        )
        print(f"[{datetime.now().strftime('%H:%M:%S')}] STEP 4/4: Done. Metrics saved.", flush=True)

        return str(uplift_csv)

    def _compute_metrics(self, train_features, feature_cols, model_info, top_k_rate):
        from autolift_ml.pipeline.train import predict_uplift
        import joblib

        model_treatment = joblib.load(model_info["model_treatment_path"])
        model_control = joblib.load(model_info["model_control_path"])

        X = train_features[feature_cols].values
        p_treatment, p_control, uplift_score = predict_uplift(model_treatment, model_control, X)

        result = train_features.copy()
        result["uplift_score"] = uplift_score

        uplift_metrics = self._calculate_uplift_metrics(result, top_k_rate)
        qini_curve = self._calculate_qini_curve(result)
        economic_summary = self._calculate_economic_summary(result, top_k_rate)

        return {
            "upliftAt10": uplift_metrics.get("uplift_at_10", 0),
            "upliftAt20": uplift_metrics.get("uplift_at_20", 0),
            "upliftAt30": uplift_metrics.get("uplift_at_30", 0),
            "auuc": uplift_metrics.get("auuc", 0),
            "qiniAuc": uplift_metrics.get("qini_auc", 0),
            "upliftCurve": uplift_metrics.get("uplift_curve", []),
            "qiniCurve": qini_curve,
            "economicSummary": economic_summary,
        }

    def _calculate_uplift_metrics(self, df, top_k_rate):
        df_sorted = df.sort_values("uplift_score", ascending=False).reset_index(drop=True)
        n = len(df_sorted)

        steps = 20
        uplift_curve = []
        for frac in np.linspace(0.05, 1.0, steps):
            k = max(1, int(n * frac))
            subset = df_sorted.head(k)

            rates = subset.groupby("treatment_flg")["target"].mean()
            treated_rate = rates.get(1, np.nan)
            control_rate = rates.get(0, np.nan)

            observed_uplift = float(treated_rate - control_rate) if pd.notna(treated_rate) and pd.notna(control_rate) else np.nan

            uplift_curve.append({
                "targetFraction": round(float(frac), 4),
                "numCustomers": k,
                "observedUplift": round(observed_uplift, 6) if pd.notna(observed_uplift) else 0,
                "treatedResponseRate": round(float(treated_rate), 6) if pd.notna(treated_rate) else None,
                "controlResponseRate": round(float(control_rate), 6) if pd.notna(control_rate) else None,
            })

        uplift_k = {}
        for k_rate in [0.10, 0.20, 0.30]:
            k = max(1, int(n * k_rate))
            top = df_sorted.head(k)
            rates = top.groupby("treatment_flg")["target"].mean()
            if 1 in rates.index and 0 in rates.index:
                uplift_k[f"uplift_at_{int(k_rate * 100)}"] = round(float(rates.loc[1] - rates.loc[0]), 6)
            else:
                uplift_k[f"uplift_at_{int(k_rate * 100)}"] = 0

        auuc = round(float(np.trapezoid(
            [p.get("observedUplift", 0) or 0 for p in uplift_curve],
            [p.get("targetFraction", 0) for p in uplift_curve]
        )), 6)

        qini_auc = self._calculate_qini_auc(df_sorted)

        return {
            **uplift_k,
            "auuc": auuc,
            "qini_auc": qini_auc,
            "uplift_curve": uplift_curve,
        }

    def _calculate_qini_curve(self, df):
        df_q = df.sort_values("uplift_score", ascending=False).reset_index(drop=True)

        y = df_q["target"].astype(float)
        treatment = df_q["treatment_flg"].astype(int)

        treated = (treatment == 1).astype(int)
        control = (treatment == 0).astype(int)

        cum_treated = treated.cumsum()
        cum_control = control.cumsum()
        cum_treated_responders = (y * treated).cumsum()
        cum_control_responders = (y * control).cumsum()

        qini = cum_treated_responders - (cum_control_responders * cum_treated / cum_control.replace(0, np.nan))
        qini = qini.fillna(0)

        n = len(df_q)
        curve = []
        for i in range(n):
            frac = (i + 1) / n
            curve.append({
                "targetFraction": round(float(frac), 4),
                "numCustomers": i + 1,
                "qini": round(float(qini.iloc[i]), 2),
                "cumTreated": int(cum_treated.iloc[i]),
                "cumControl": int(cum_control.iloc[i]),
                "cumTreatedResponders": int(cum_treated_responders.iloc[i]),
                "cumControlResponders": int(cum_control_responders.iloc[i]),
            })

        return curve

    def _calculate_qini_auc(self, df):
        df_q = df.sort_values("uplift_score", ascending=False).reset_index(drop=True)

        y = df_q["target"].astype(float)
        treatment = df_q["treatment_flg"].astype(int)

        treated = (treatment == 1).astype(int)
        control = (treatment == 0).astype(int)

        cum_treated = treated.cumsum()
        cum_control = control.cumsum()
        cum_treated_responders = (y * treated).cumsum()
        cum_control_responders = (y * control).cumsum()

        qini = cum_treated_responders - (cum_control_responders * cum_treated / cum_control.replace(0, np.nan))
        qini = qini.fillna(0).values

        n = len(df_q)
        percentages = np.arange(1, n + 1) / n
        random_qini = np.array([0.0] + qini[:-1].tolist())

        return round(float(np.trapezoid(qini - random_qini, percentages)), 2)

    def _calculate_economic_summary(self, df, top_k_rate):
        VALUE_PER_CONVERSION = 100_000
        PROMO_COST_PER_TARGET = 10_000

        total_customers = len(df)

        mass_campaign_targets = int(total_customers * 0.5)
        targeted_targets = int(total_customers * top_k_rate)

        strategies = [
            ("Mass Campaign", df.index),
            ("Response Targeting", df.sort_values("uplift_score", ascending=False).head(targeted_targets).index),
            ("Uplift Targeting", df[df["uplift_score"] >= df["uplift_score"].quantile(1 - top_k_rate)].index),
        ]

        summary = []
        for strategy_name, selected_idx in strategies:
            subset = df.loc[selected_idx]

            treated_rate = subset[subset["treatment_flg"] == 1]["target"].mean() if (subset["treatment_flg"] == 1).any() else subset["target"].mean()
            control_rate = subset[subset["treatment_flg"] == 0]["target"].mean() if (subset["treatment_flg"] == 0).any() else 0

            incremental_rate = max(0, float(treated_rate - control_rate))
            expected_incremental_conversions = len(subset) * incremental_rate
            promotion_cost = len(subset) * PROMO_COST_PER_TARGET
            expected_revenue = expected_incremental_conversions * VALUE_PER_CONVERSION
            net_profit = expected_revenue - promotion_cost

            summary.append({
                "strategy": strategy_name,
                "numTargeted": len(subset),
                "expectedIncrementalConversions": round(expected_incremental_conversions, 2),
                "promotionCost": promotion_cost,
                "expectedRevenue": round(expected_revenue, 2),
                "netProfit": round(net_profit, 2),
            })

        return summary

    def _run_gp_pipeline(self, conn, job_id, campaign_id: str, input_params: dict, uplift_score_job_id: Optional[str]) -> str:
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