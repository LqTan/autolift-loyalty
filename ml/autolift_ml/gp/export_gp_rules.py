"""Export GP rules to CSV for Spring Boot import."""
import pandas as pd
from pathlib import Path
from datetime import datetime


def export_gp_rules(
    rules: list,
    campaign_id: str,
    model_version: str,
    output_path: Path,
    source_file: str = "gp_rules.csv",
) -> pd.DataFrame:
    rows = []
    for rule in rules:
        rows.append({
            "id": None,
            "campaign_id": campaign_id,
            "rule_text": rule.get("rule_text", ""),
            "rule_expression": rule.get("rule_expression", ""),
            "target_label": "PERSUADABLE",
            "precision_value": rule.get("precision", 0.0),
            "recall_value": rule.get("recall", 0.0),
            "f1_score": rule.get("f1", 0.0),
            "accuracy_value": rule.get("accuracy", 0.0),
            "coverage_value": rule.get("coverage", 0.0),
            "model_version": model_version,
            "source_file": source_file,
            "created_at": datetime.now().isoformat(),
        })

    df = pd.DataFrame(rows)
    output_path = Path(output_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    df.to_csv(output_path, index=False)

    print(f"GP rules exported to {output_path}")
    print(f"  Total rules: {len(df)}")
    return df


if __name__ == "__main__":
    from pathlib import Path

    BASE_DIR = Path("/home/archer/Projects/java_projects/autolift-loyalty/ml")
    ARTIFACTS_DIR = BASE_DIR / "artifacts" / "outputs"

    rules = [
        {
            "rule_text": "(recency_days < 14) AND (frequency_total > 5)",
            "rule_expression": "(recency_days < 14) AND (frequency_total > 5)",
            "precision": 0.75,
            "recall": 0.60,
            "f1": 0.667,
            "accuracy": 0.70,
            "coverage": 0.15,
        },
        {
            "rule_text": "(avg_basket_value > 15000) AND (unique_product_count > 30)",
            "rule_expression": "(avg_basket_value > 15000) AND (unique_product_count > 30)",
            "precision": 0.72,
            "recall": 0.55,
            "f1": 0.625,
            "accuracy": 0.68,
            "coverage": 0.12,
        },
    ]

    export_gp_rules(
        rules=rules,
        campaign_id="x5-campaign-v1",
        model_version="v1",
        output_path=ARTIFACTS_DIR / "gp_rules.csv",
    )