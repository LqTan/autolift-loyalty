"""Build GP input dataset from uplift scores and feature snapshots."""
import pandas as pd
from pathlib import Path


def load_uplift_scores(csv_path: Path) -> pd.DataFrame:
    df = pd.read_csv(csv_path)
    df.columns = df.columns.str.strip()
    return df


def load_feature_snapshots(csv_path: Path) -> pd.DataFrame:
    df = pd.read_csv(csv_path)
    df.columns = df.columns.str.strip()
    return df


def build_gp_input(
    uplift_scores_path: Path,
    feature_snapshots_path: Path,
    output_path: Path,
    top_k_rate: float = 0.20,
) -> pd.DataFrame:
    uplift_df = load_uplift_scores(uplift_scores_path)
    feature_df = load_feature_snapshots(feature_snapshots_path)

    uplift_df.columns = uplift_df.columns.str.strip()
    feature_df.columns = feature_df.columns.str.strip()

    if "customer_id" not in uplift_df.columns or "uplift_score" not in uplift_df.columns:
        raise ValueError(
            f"uplift_scores must have customer_id and uplift_score. "
            f"Columns: {uplift_df.columns.tolist()}"
        )

    if "customer_id" not in feature_df.columns:
        raise ValueError(
            f"feature_snapshots must have customer_id. Columns: {feature_df.columns.tolist()}"
        )

    merged = uplift_df.merge(feature_df, on="customer_id", how="inner", suffixes=("", "_feat"))

    campaign_id = merged["campaign_id"].iloc[0] if "campaign_id" in merged.columns else "unknown"
    merged["campaign_id_gp"] = campaign_id

    numeric_cols = [
        "recency_days", "frequency_total", "monetary_total",
        "avg_basket_value", "total_quantity",
        "unique_product_count", "unique_category_count",
    ]
    for col in numeric_cols:
        if col in merged.columns:
            merged[col] = pd.to_numeric(merged[col], errors="coerce").fillna(0)

    merged = merged.sort_values("uplift_score", ascending=False)
    top_n = int(len(merged) * top_k_rate)
    merged["target_flag"] = 0
    if top_n > 0:
        merged.loc[merged.head(top_n).index, "target_flag"] = 1

    gp_cols = [
        "customer_id", "campaign_id_gp",
        "recency_days", "frequency_total", "monetary_total",
        "avg_basket_value", "total_quantity",
        "unique_product_count", "unique_category_count",
        "favorite_category",
        "uplift_score", "target_flag",
    ]
    available_cols = [c for c in gp_cols if c in merged.columns]
    gp_df = merged[available_cols].copy()

    if "favorite_category" in gp_df.columns:
        gp_df["favorite_category"] = gp_df["favorite_category"].fillna("unknown").astype(str)

    output_path = Path(output_path)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    gp_df.to_csv(output_path, index=False)

    print(f"GP input saved to {output_path}")
    print(f"  Total rows: {len(gp_df)}")
    print(f"  Target=1 (top {top_k_rate*100:.0f}%): {gp_df['target_flag'].sum()}")
    print(f"  Target=0: {(gp_df['target_flag'] == 0).sum()}")

    return gp_df


if __name__ == "__main__":
    from pathlib import Path

    BASE_DIR = Path("/home/archer/Projects/java_projects/autolift-loyalty/ml")
    ARTIFACTS_DIR = BASE_DIR / "artifacts" / "outputs"

    gp_input_path = ARTIFACTS_DIR / "gp_input.csv"

    build_gp_input(
        uplift_scores_path=ARTIFACTS_DIR / "customer_uplift_scores.csv",
        feature_snapshots_path=ARTIFACTS_DIR / "customer_feature_snapshots.csv",
        output_path=gp_input_path,
        top_k_rate=0.20,
    )