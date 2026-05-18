"""Uplift modeling metrics: Qini, AUUC, Uplift@K."""
import numpy as np
import pandas as pd


def uplift_at_k(
    df: pd.DataFrame,
    treatment_col: str = "treatment_flg",
    target_col: str = "target",
    uplift_col: str = "uplift_score",
    k: float = 0.30
) -> float:
    """Calculate Uplift@K metric.

    Args:
        df: DataFrame with treatment, target, and uplift_score columns
        treatment_col: Name of treatment column
        target_col: Name of target column
        uplift_col: Name of uplift score column
        k: Top K fraction to consider (0.0 to 1.0)

    Returns:
        Uplift@K value
    """
    df = df.sort_values(uplift_col, ascending=False).head(int(len(df) * k))

    treatment = df[treatment_col] == 1
    control = df[treatment_col] == 0

    if treatment.sum() == 0 or control.sum() == 0:
        return 0.0

    target_rate_treatment = df.loc[treatment, target_col].mean()
    target_rate_control = df.loc[control, target_col].mean()

    return target_rate_treatment - target_rate_control


def qini_curve(
    df: pd.DataFrame,
    treatment_col: str = "treatment_flg",
    target_col: str = "target",
    uplift_col: str = "uplift_score",
    n_points: int = 100
) -> tuple[list[float], list[float]]:
    """Calculate Qini curve.

    Args:
        df: DataFrame with treatment, target, and uplift_score columns
        treatment_col: Name of treatment column
        target_col: Name of target column
        uplift_col: Name of uplift score column
        n_points: Number of points in the curve

    Returns:
        Tuple of (percentages, qini_values)
    """
    df = df.sort_values(uplift_col, ascending=False).reset_index(drop=True)

    n = len(df)
    step = max(1, n // n_points)

    percentages = []
    qini_values = []

    for i in range(step, n + 1, step):
        head = df.head(i)
        tail = df.tail(n - i)

        treatment_head = head[treatment_col] == 1
        control_head = head[treatment_col] == 0
        treatment_tail = tail[treatment_col] == 1
        control_tail = tail[treatment_col] == 0

        if treatment_head.sum() == 0 or control_head.sum() == 0:
            continue

        conversion_treatment = head.loc[treatment_head, target_col].sum()
        conversion_control = head.loc[control_head, target_col].sum()

        n_treatment = treatment_head.sum()
        n_control = control_head.sum()

        uplift_head = conversion_treatment / n_treatment - conversion_control / n_control

        percentages.append(i / n)
        qini_values.append(uplift_head * (n_treatment + n_control) / n)

    return percentages, qini_values


def auuc(
    df: pd.DataFrame,
    treatment_col: str = "treatment_flg",
    target_col: str = "target",
    uplift_col: str = "uplift_score"
) -> float:
    """Calculate Area Under Uplift Curve (AUUC).

    Args:
        df: DataFrame with treatment, target, and uplift_score columns
        treatment_col: Name of treatment column
        target_col: Name of target column
        uplift_col: Name of uplift score column

    Returns:
        AUUC value
    """
    percentages, qini_values = qini_curve(df, treatment_col, target_col, uplift_col)

    if not percentages:
        return 0.0

    return np.trapezoid(qini_values, percentages)


def qini_auc(
    df: pd.DataFrame,
    treatment_col: str = "treatment_flg",
    target_col: str = "target",
    uplift_col: str = "uplift_score"
) -> float:
    """Calculate Qini AUC.

    Simplified Qini AUC - area between Qini curve and random targeting.

    Args:
        df: DataFrame with treatment, target, and uplift_score columns
        treatment_col: Name of treatment column
        target_col: Name of target column
        uplift_col: Name of uplift score column

    Returns:
        Qini AUC value
    """
    percentages, qini_values = qini_curve(df, treatment_col, target_col, uplift_col)

    if not percentages:
        return 0.0

    qini_values = np.array(qini_values)
    percentages = np.array(percentages)

    random_qini = np.array([0.0] + qini_values[:-1].tolist())

    return np.trapezoid(qini_values - random_qini, percentages)


def print_evaluation_report(
    df: pd.DataFrame,
    treatment_col: str = "treatment_flg",
    target_col: str = "target",
    uplift_col: str = "uplift_score",
    top_k_rates: list[float] = [0.10, 0.20, 0.30]
) -> None:
    """Print evaluation report."""
    print("\n" + "=" * 50)
    print("UPLIFT MODEL EVALUATION REPORT")
    print("=" * 50)

    print(f"\nDataset size: {len(df)}")
    print(f"Treatment rate: {df[treatment_col].mean():.4f}")
    print(f"Target rate: {df[target_col].mean():.4f}")

    for k in top_k_rates:
        uplift = uplift_at_k(df, treatment_col, target_col, uplift_col, k)
        print(f"\nUplift@{k:.0%}: {uplift:.6f}")

    auuc_value = auuc(df, treatment_col, target_col, uplift_col)
    qini = qini_auc(df, treatment_col, target_col, uplift_col)

    print(f"\nAUUC: {auuc_value:.6f}")
    print(f"Qini AUC: {qini:.6f}")
    print("=" * 50 + "\n")