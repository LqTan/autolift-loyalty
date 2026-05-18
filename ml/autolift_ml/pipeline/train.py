"""Train T-Learner uplift model."""
import joblib
from pathlib import Path
from typing import Literal

import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split


def train_t_learner(
    train_features: pd.DataFrame,
    model_dir: Path,
    base_model: str = "RandomForestClassifier",
    n_estimators: int = 100,
    random_state: int = 42,
    test_size: float = 0.2,
) -> dict:
    """Train T-Learner uplift models.

    Args:
        train_features: DataFrame with treatment_flg, target, and features
        model_dir: Directory to save models
        base_model: Base model type ("RandomForestClassifier")
        n_estimators: Number of estimators for RandomForest
        random_state: Random seed
        test_size: Test set fraction

    Returns:
        Dictionary with model info and metrics
    """
    model_dir = Path(model_dir)
    model_dir.mkdir(parents=True, exist_ok=True)

    feature_cols = [
        col for col in train_features.columns
        if col not in ["customer_id", "treatment_flg", "target"]
    ]

    X = train_features[feature_cols].values
    T = train_features["treatment_flg"].values
    Y = train_features["target"].values

    X_train, X_test, T_train, T_test, Y_train, Y_test = train_test_split(
        X, T, Y, test_size=test_size, random_state=random_state
    )

    mask_treatment = T_train == 1
    mask_control = T_train == 0

    if base_model == "RandomForestClassifier":
        model_class = RandomForestClassifier
        model_kwargs = {"n_estimators": n_estimators, "random_state": random_state, "n_jobs": -1}
    else:
        raise ValueError(f"Unsupported base model: {base_model}")

    print("Training treatment model...")
    model_treatment = model_class(**model_kwargs)
    model_treatment.fit(X_train[mask_treatment], Y_train[mask_treatment])

    print("Training control model...")
    model_control = model_class(**model_kwargs)
    model_control.fit(X_train[mask_control], Y_train[mask_control])

    treatment_path = model_dir / "model_treatment_v1.joblib"
    control_path = model_dir / "model_control_v1.joblib"

    joblib.dump(model_treatment, treatment_path)
    joblib.dump(model_control, control_path)

    print(f"\nModels saved to {model_dir}")
    print(f"  - {treatment_path}")
    print(f"  - {control_path}")

    return {
        "model_treatment_path": str(treatment_path),
        "model_control_path": str(control_path),
        "feature_cols": feature_cols,
        "n_estimators": n_estimators,
        "random_state": random_state,
    }


def predict_uplift(
    model_treatment,
    model_control,
    X,
) -> tuple[pd.Series, pd.Series, pd.Series]:
    """Predict uplift scores.

    Args:
        model_treatment: Trained treatment model
        model_control: Trained control model
        X: Feature matrix

    Returns:
        Tuple of (p_treatment, p_control, uplift_score)
    """
    p_treatment = model_treatment.predict_proba(X)[:, 1]
    p_control = model_control.predict_proba(X)[:, 1]
    uplift_score = p_treatment - p_control

    return p_treatment, p_control, uplift_score


def score_customers(
    train_features: pd.DataFrame,
    model_info: dict,
) -> pd.DataFrame:
    """Score customers with uplift models.

    Args:
        train_features: DataFrame with features
        model_info: Dictionary from train_t_learner

    Returns:
        DataFrame with uplift scores
    """
    model_treatment = joblib.load(model_info["model_treatment_path"])
    model_control = joblib.load(model_info["model_control_path"])

    feature_cols = model_info["feature_cols"]
    X = train_features[feature_cols].values

    p_treatment, p_control, uplift_score = predict_uplift(
        model_treatment, model_control, X
    )

    result = train_features[["customer_id", "treatment_flg", "target"]].copy()
    result["p_treatment"] = p_treatment
    result["p_control"] = p_control
    result["uplift_score"] = uplift_score

    return result


if __name__ == "__main__":
    from pathlib import Path

    BASE_DIR = Path("/content/AutoLift_DM_Materials")
    OUTPUT_DIR = BASE_DIR / "02_processed_data"
    MODEL_DIR = BASE_DIR / "03_uplift_outputs" / "models"

    train_features = pd.read_csv(OUTPUT_DIR / "x5_customer_features_train.csv")

    model_info = train_t_learner(
        train_features=train_features,
        model_dir=MODEL_DIR,
        base_model="RandomForestClassifier",
        n_estimators=100,
        random_state=42,
    )

    print("\nModel training complete!")