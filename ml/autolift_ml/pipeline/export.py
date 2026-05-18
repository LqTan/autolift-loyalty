"""Export uplift scores for Spring Boot import."""
import joblib
from datetime import datetime
from pathlib import Path

import numpy as np
import pandas as pd

from autolift_ml.pipeline.train import predict_uplift


PERSUADABLE_THRESHOLD = 0.05
NOT_TARGET_THRESHOLD = -0.01


def segment_from_uplift(uplift_score: float) -> str:
    """Assign segment based on uplift score.

    Args:
        uplift_score: Uplift score value

    Returns:
        Segment name
    """
    if uplift_score >= PERSUADABLE_THRESHOLD:
        return "PERSUADABLE"
    elif uplift_score <= NOT_TARGET_THRESHOLD:
        return "DO_NOT_TARGET"
    else:
        return "NEUTRAL"


def export_uplift_scores(
    test_features: pd.DataFrame,
    model_info: dict,
    model_version: str,
    output_dir: Path,
    campaign_id: str = "x5-campaign-v1",
) -> Path:
    """Export uplift scores CSV for Spring Boot import.

    Args:
        test_features: DataFrame with features (no treatment/target)
        model_info: Dictionary from train_t_learner
        model_version: Version string for models
        output_dir: Directory to save output CSV
        campaign_id: Campaign ID to assign

    Returns:
        Path to saved CSV
    """
    model_treatment = joblib.load(model_info["model_treatment_path"])
    model_control = joblib.load(model_info["model_control_path"])

    feature_cols = model_info["feature_cols"]
    X = test_features[feature_cols].values

    p_treatment, p_control, uplift_score = predict_uplift(
        model_treatment, model_control, X
    )

    result = pd.DataFrame({
        "customer_id": test_features["customer_id"],
        "campaign_id": campaign_id,
        "uplift_score": uplift_score,
        "treatment_probability": p_treatment,
        "control_probability": p_control,
        "segment": [segment_from_uplift(u) for u in uplift_score],
        "model_version": model_version,
        "scored_at": datetime.now().isoformat(),
    })

    result = result.sort_values("uplift_score", ascending=False).reset_index(drop=True)
    result["rank"] = range(1, len(result) + 1)

    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    output_path = output_dir / "customer_uplift_scores.csv"
    result.to_csv(output_path, index=False)

    print(f"Exported {len(result)} rows to {output_path}")
    print(f"  - PERSUADABLE: {(result['segment'] == 'PERSUADABLE').sum()}")
    print(f"  - NEUTRAL: {(result['segment'] == 'NEUTRAL').sum()}")
    print(f"  - DO_NOT_TARGET: {(result['segment'] == 'DO_NOT_TARGET').sum()}")

    return output_path


def export_feature_snapshots(
    test_features: pd.DataFrame,
    model_version: str,
    output_dir: Path,
    campaign_id: str = "x5-campaign-v1",
) -> Path:
    """Export feature snapshots CSV for Spring Boot import.

    Args:
        test_features: DataFrame with features
        model_version: Version string
        output_dir: Directory to save output CSV
        campaign_id: Campaign ID to assign

    Returns:
        Path to saved CSV
    """
    feature_snapshot_cols = [
        "customer_id",
        "recency_days",
        "frequency_total",
        "monetary_total",
        "avg_basket_value",
        "total_quantity",
        "unique_product_count",
        "unique_category_count",
    ]

    available_cols = [c for c in feature_snapshot_cols if c in test_features.columns]

    result = test_features[available_cols].copy()
    result["campaign_id"] = campaign_id
    result["feature_version"] = model_version
    result["created_at"] = datetime.now().isoformat()

    if "favorite_category" not in result.columns:
        result["favorite_category"] = "unknown"

    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    output_path = output_dir / "customer_feature_snapshots.csv"
    result.to_csv(output_path, index=False)

    print(f"Exported {len(result)} feature snapshots to {output_path}")

    return output_path


if __name__ == "__main__":
    from pathlib import Path

    BASE_DIR = Path("/content/AutoLift_DM_Materials")
    OUTPUT_DIR = BASE_DIR / "02_processed_data"
    MODEL_DIR = BASE_DIR / "03_uplift_outputs" / "models"
    EXPORT_DIR = BASE_DIR / "03_uplift_outputs"

    test_features = pd.read_csv(OUTPUT_DIR / "x5_customer_features_test.csv")

    model_info = {
        "model_treatment_path": MODEL_DIR / "model_treatment_v1.joblib",
        "model_control_path": MODEL_DIR / "model_control_v1.joblib",
        "feature_cols": [
            col for col in test_features.columns
            if col not in ["customer_id"]
        ],
    }

    export_uplift_scores(
        test_features=test_features,
        model_info=model_info,
        model_version="v1",
        output_dir=EXPORT_DIR,
    )

    export_feature_snapshots(
        test_features=test_features,
        model_version="v1",
        output_dir=EXPORT_DIR,
    )