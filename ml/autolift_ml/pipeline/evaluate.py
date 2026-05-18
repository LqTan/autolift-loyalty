"""Evaluate uplift model."""
import joblib
from pathlib import Path

import pandas as pd

from autolift_ml.pipeline.train import predict_uplift
from autolift_ml.utils.metrics import (
    uplift_at_k,
    qini_curve,
    auuc,
    qini_auc,
    print_evaluation_report,
)


def evaluate_model(
    train_features: pd.DataFrame,
    model_info: dict,
    top_k_rates: list[float] = [0.10, 0.20, 0.30],
) -> dict:
    """Evaluate uplift model on training data.

    Args:
        train_features: DataFrame with treatment_flg, target, and features
        model_info: Dictionary from train_t_learner
        top_k_rates: List of K fractions for Uplift@K

    Returns:
        Dictionary with evaluation metrics
    """
    model_treatment = joblib.load(model_info["model_treatment_path"])
    model_control = joblib.load(model_info["model_control_path"])

    feature_cols = model_info["feature_cols"]
    X = train_features[feature_cols].values

    p_treatment, p_control, uplift_score = predict_uplift(
        model_treatment, model_control, X
    )

    result = train_features.copy()
    result["p_treatment"] = p_treatment
    result["p_control"] = p_control
    result["uplift_score"] = uplift_score

    print_evaluation_report(result, top_k_rates=top_k_rates)

    uplift_k_results = {}
    for k in top_k_rates:
        uplift_k_results[f"uplift_at_{int(k*100)}"] = uplift_at_k(result, k=k)

    return {
        "auuc": auuc(result),
        "qini_auc": qini_auc(result),
        "uplift_k": uplift_k_results,
        "n_samples": len(result),
    }


if __name__ == "__main__":
    from pathlib import Path

    BASE_DIR = Path("/content/AutoLift_DM_Materials")
    OUTPUT_DIR = BASE_DIR / "02_processed_data"
    MODEL_DIR = BASE_DIR / "03_uplift_outputs" / "models"

    train_features = pd.read_csv(OUTPUT_DIR / "x5_customer_features_train.csv")

    model_info = {
        "model_treatment_path": MODEL_DIR / "model_treatment_v1.joblib",
        "model_control_path": MODEL_DIR / "model_control_v1.joblib",
        "feature_cols": [
            col for col in train_features.columns
            if col not in ["customer_id", "treatment_flg", "target"]
        ],
    }

    evaluate_model(train_features, model_info)