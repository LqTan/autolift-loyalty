"""GP Pipeline: Build input -> Train rules -> Export for Spring Boot."""
import argparse
import sys
from datetime import datetime
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from autolift_ml.gp.build_gp_input import build_gp_input
from autolift_ml.gp.train_gp_rules import train_gp_rules
from autolift_ml.gp.export_gp_rules import export_gp_rules


def run_gp_pipeline(
    uplift_scores_path: Path,
    feature_snapshots_path: Path,
    output_dir: Path,
    campaign_id: str = "x5-campaign-v1",
    model_version: str = "v1",
    top_k_rate: float = 0.20,
    population_size: int = 200,
    generations: int = 20,
) -> dict:
    """Run the complete GP rule extraction pipeline."""
    start_time = datetime.now()

    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    print("=" * 60)
    print("AUTOLIFT GP RULE EXTRACTION PIPELINE")
    print("=" * 60)
    print(f"\nCampaign: {campaign_id}")
    print(f"Model version: {model_version}")
    print(f"Start time: {start_time.isoformat()}")

    print("\n" + "=" * 60)
    print("STEP 1: BUILD GP INPUT")
    print("=" * 60)

    gp_input_path = output_dir / "gp_input.csv"
    build_gp_input(
        uplift_scores_path=uplift_scores_path,
        feature_snapshots_path=feature_snapshots_path,
        output_path=gp_input_path,
        top_k_rate=top_k_rate,
    )

    print("\n" + "=" * 60)
    print("STEP 2: TRAIN GP RULES")
    print("=" * 60)

    gp_df, rules = train_gp_rules(
        gp_input_path=gp_input_path,
        output_dir=output_dir,
        population_size=population_size,
        generations=generations,
    )

    print(f"\nTrained {len(rules)} GP rules:")
    for r in rules:
        print(f"  {r['rule_text']}")
        print(f"    f1={r['f1']:.3f}, precision={r['precision']:.3f}, recall={r['recall']:.3f}")

    print("\n" + "=" * 60)
    print("STEP 3: EXPORT GP RULES")
    print("=" * 60)

    gp_rules_path = output_dir / "gp_rules.csv"
    export_gp_rules(
        rules=rules,
        campaign_id=campaign_id,
        model_version=model_version,
        output_path=gp_rules_path,
        source_file="gp_rules.csv",
    )

    end_time = datetime.now()
    duration = end_time - start_time

    print("\n" + "=" * 60)
    print("GP PIPELINE COMPLETE")
    print("=" * 60)
    print(f"Duration: {duration}")
    print(f"Start: {start_time.isoformat()}")
    print(f"End: {end_time.isoformat()}")
    print(f"\nOutput file: {gp_rules_path}")

    return {
        "gp_input": str(gp_input_path),
        "gp_rules": str(gp_rules_path),
        "rules_count": len(rules),
        "duration": str(duration),
    }


def main():
    parser = argparse.ArgumentParser(description="Autolift GP Rule Extraction Pipeline")
    parser.add_argument(
        "--uplift-scores", "-u",
        type=str,
        default="artifacts/outputs/customer_uplift_scores.csv",
        help="Path to uplift scores CSV"
    )
    parser.add_argument(
        "--feature-snapshots", "-f",
        type=str,
        default="artifacts/outputs/customer_feature_snapshots.csv",
        help="Path to feature snapshots CSV"
    )
    parser.add_argument(
        "--output-dir", "-o",
        type=str,
        default="artifacts/outputs",
        help="Output directory for GP pipeline"
    )
    parser.add_argument(
        "--campaign-id", "-c",
        type=str,
        default="x5-campaign-v1",
        help="Campaign ID for GP rules"
    )
    parser.add_argument(
        "--model-version", "-v",
        type=str,
        default="v1",
        help="Model version string"
    )
    parser.add_argument(
        "--top-k-rate", "-k",
        type=float,
        default=0.20,
        help="Top K%% rate for target flag (default: 0.20)"
    )
    parser.add_argument(
        "--population-size", "-p",
        type=int,
        default=200,
        help="GP population size (default: 200)"
    )
    parser.add_argument(
        "--generations", "-g",
        type=int,
        default=20,
        help="GP generations (default: 20)"
    )

    args = parser.parse_args()

    ml_dir = Path(__file__).parent.parent
    uplift_scores = ml_dir / args.uplift_scores if not Path(args.uplift_scores).is_absolute() else Path(args.uplift_scores)
    feature_snapshots = ml_dir / args.feature_snapshots if not Path(args.feature_snapshots).is_absolute() else Path(args.feature_snapshots)
    output_dir = ml_dir / args.output_dir if not Path(args.output_dir).is_absolute() else Path(args.output_dir)

    result = run_gp_pipeline(
        uplift_scores_path=uplift_scores,
        feature_snapshots_path=feature_snapshots,
        output_dir=output_dir,
        campaign_id=args.campaign_id,
        model_version=args.model_version,
        top_k_rate=args.top_k_rate,
        population_size=args.population_size,
        generations=args.generations,
    )

    if result:
        print("\nGP Pipeline succeeded!")
        sys.exit(0)
    else:
        print("\nGP Pipeline failed!")
        sys.exit(1)


if __name__ == "__main__":
    main()