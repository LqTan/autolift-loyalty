"""Build customer-level features from X5 RetailHero dataset."""
import gc
from pathlib import Path
from typing import Optional

import numpy as np
import pandas as pd

from autolift_ml.utils.data_loader import (
    infer_column,
    normalize_id_column,
    safe_read_csv,
    encode_non_numeric,
    fill_numeric_na,
)


DEFAULT_CHUNKSIZE = 300_000


def aggregate_purchases_chunked(
    purchases_path: Path,
    train_customers: Optional[pd.Series] = None,
    chunksize: int = DEFAULT_CHUNKSIZE,
    full_scan: bool = True,
    max_rows: Optional[int] = None,
) -> pd.DataFrame:
    """Aggregate purchase data in chunks to avoid OOM.

    Args:
        purchases_path: Path to purchases.csv.gz
        train_customers: Filter to only these customers
        chunksize: Rows per chunk
        full_scan: If True, scan all purchases (not just max_rows)
        max_rows: Stop after this many rows (if full_scan=False)

    Returns:
        DataFrame with aggregated purchase features per customer
    """
    if purchases_path is None or not Path(purchases_path).exists():
        raise FileNotFoundError("Missing required purchases file.")

    raw_header = pd.read_csv(purchases_path, nrows=0)
    raw_columns = list(raw_header.columns)

    raw_id_col = infer_column(raw_header, ["customer_id", "client_id"])
    tx_col = infer_column(raw_header, ["transaction_id", "receipt_id", "check_id"])
    date_col = infer_column(raw_header, ["transaction_datetime", "transaction_date", "date"])
    amount_col = infer_column(raw_header, ["purchase_sum", "trn_sum_from_iss", "trn_sum_from_red", "amount", "sum"])
    qty_col = infer_column(raw_header, ["product_quantity", "quantity", "qty"])
    product_col = infer_column(raw_header, ["product_id", "sku", "sku_id"])
    category_col = infer_column(raw_header, ["level_1", "level_2", "category_id", "category"])

    if raw_id_col is None:
        raise ValueError(
            f"Cannot find customer_id/client_id in purchases file. "
            f"Available columns: {raw_columns}"
        )

    usecols = [
        c for c in [
            raw_id_col,
            tx_col,
            date_col,
            amount_col,
            qty_col,
            product_col,
            category_col,
        ]
        if c is not None
    ]
    usecols = list(dict.fromkeys(usecols))

    print(f"Purchases columns used: {usecols}")

    keep_customers = None
    if train_customers is not None:
        keep_customers = set(pd.Series(train_customers).astype(str))

    agg_parts = []
    rows_seen = 0
    max_dates = []

    reader = pd.read_csv(purchases_path, usecols=usecols, chunksize=chunksize)

    for chunk_idx, chunk in enumerate(reader, start=1):
        rows_seen += len(chunk)

        chunk = normalize_id_column(chunk)

        if "customer_id" not in chunk.columns:
            raise ValueError(
                "Cannot find customer_id/client_id in purchases file after normalization."
            )

        chunk["customer_id"] = chunk["customer_id"].astype(str)

        if keep_customers is not None:
            chunk = chunk[chunk["customer_id"].isin(keep_customers)]

        if len(chunk) == 0:
            if (not full_scan) and max_rows is not None and rows_seen >= max_rows:
                break
            continue

        tmp = pd.DataFrame({"customer_id": chunk["customer_id"]})
        tmp["row_count"] = 1

        if tx_col and tx_col in chunk.columns:
            tmp["transaction_id"] = chunk[tx_col]
        else:
            tmp["transaction_id"] = np.arange(len(chunk))

        if amount_col and amount_col in chunk.columns:
            tmp["amount"] = pd.to_numeric(chunk[amount_col], errors="coerce").fillna(0)
        else:
            tmp["amount"] = 0.0

        if qty_col and qty_col in chunk.columns:
            tmp["quantity"] = pd.to_numeric(chunk[qty_col], errors="coerce").fillna(0)
        else:
            tmp["quantity"] = 1.0

        if product_col and product_col in chunk.columns:
            tmp["product_id"] = chunk[product_col].astype(str)
        else:
            tmp["product_id"] = "unknown"

        if category_col and category_col in chunk.columns:
            tmp["category_id"] = chunk[category_col].astype(str)
        else:
            tmp["category_id"] = "unknown"

        if date_col and date_col in chunk.columns:
            dates = pd.to_datetime(chunk[date_col], errors="coerce")
            tmp["last_purchase_dt"] = dates

            if dates.notna().any():
                max_dates.append(dates.max())
        else:
            tmp["last_purchase_dt"] = pd.NaT

        grouped_chunk = tmp.groupby("customer_id").agg(
            purchase_rows=("row_count", "sum"),
            frequency_total=("transaction_id", "nunique"),
            monetary_total=("amount", "sum"),
            total_quantity=("quantity", "sum"),
            unique_product_count=("product_id", "nunique"),
            unique_category_count=("category_id", "nunique"),
            last_purchase_dt=("last_purchase_dt", "max"),
        ).reset_index()

        agg_parts.append(grouped_chunk)

        if chunk_idx % 5 == 0:
            print(f"Processed chunks: {chunk_idx}, rows seen: {rows_seen:,}")

        del chunk, tmp, grouped_chunk
        gc.collect()

        if (not full_scan) and max_rows is not None and rows_seen >= max_rows:
            print(f"Stopped at max_rows={max_rows:,}.")
            break

    if not agg_parts:
        return pd.DataFrame(columns=["customer_id"])

    all_agg = pd.concat(agg_parts, ignore_index=True)

    out = all_agg.groupby("customer_id").agg(
        purchase_rows=("purchase_rows", "sum"),
        frequency_total=("frequency_total", "sum"),
        monetary_total=("monetary_total", "sum"),
        total_quantity=("total_quantity", "sum"),
        unique_product_count=("unique_product_count", "sum"),
        unique_category_count=("unique_category_count", "sum"),
        last_purchase_dt=("last_purchase_dt", "max"),
    ).reset_index()

    if max_dates:
        ref_date = max(max_dates)
        out["recency_days"] = (
            ref_date - pd.to_datetime(out["last_purchase_dt"], errors="coerce")
        ).dt.days

        if out["recency_days"].notna().any():
            out["recency_days"] = out["recency_days"].fillna(out["recency_days"].median())
        else:
            out["recency_days"] = 999
    else:
        out["recency_days"] = 999

    out["avg_basket_value"] = out["monetary_total"] / out["frequency_total"].replace(0, np.nan)
    out["avg_basket_value"] = out["avg_basket_value"].fillna(0)

    out = out.drop(columns=["last_purchase_dt"], errors="ignore")
    return out


def build_features(
    base_dir: Path,
    train_file: str,
    clients_file: str,
    purchases_file: str,
    output_dir: Path,
    chunksize: int = DEFAULT_CHUNKSIZE,
) -> tuple[pd.DataFrame, pd.DataFrame, str]:
    """Build features from X5 dataset.

    Args:
        base_dir: Base directory containing raw data
        train_file: Name of train file
        clients_file: Name of clients file
        purchases_file: Name of purchases file
        output_dir: Directory to save output CSVs
        chunksize: Chunk size for purchases processing

    Returns:
        Tuple of (train_features, test_features, data_source)
    """
    train_df, clients_df, purchases_path = safe_read_csv(
        Path(base_dir) / train_file
    ), None, Path(base_dir) / purchases_file

    if train_df is None:
        raise FileNotFoundError("Missing required X5 files.")

    train_df["customer_id"] = train_df["customer_id"].astype(str)
    train_customers = train_df["customer_id"]

    purchase_features = aggregate_purchases_chunked(
        purchases_path=purchases_path,
        train_customers=train_customers,
        chunksize=chunksize,
    )

    purchase_features["customer_id"] = purchase_features["customer_id"].astype(str)

    train_features = train_df.merge(
        purchase_features,
        on="customer_id",
        how="left",
    )

    if clients_df is not None:
        clients_df["customer_id"] = clients_df["customer_id"].astype(str)
        train_features = train_features.merge(
            clients_df,
            on="customer_id",
            how="left",
            suffixes=("", "_client"),
        )

    test_features = train_features.drop(
        columns=["treatment_flg", "target"],
        errors="ignore",
    ).sample(
        frac=0.2,
        random_state=42,
    ).reset_index(drop=True)

    data_source = "x5_retailhero"

    train_features = encode_non_numeric(train_features)
    test_features = encode_non_numeric(test_features)

    train_features = fill_numeric_na(train_features)
    test_features = fill_numeric_na(test_features)

    if "treatment_flg" not in train_features.columns:
        raise ValueError("Train data must contain treatment_flg")

    if "target" not in train_features.columns:
        raise ValueError("Train data must contain target")

    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    train_features.to_csv(output_dir / "x5_customer_features_train.csv", index=False)
    test_features.to_csv(output_dir / "x5_customer_features_test.csv", index=False)

    print(f"\ndata_source: {data_source}")
    print(f"train_features: {train_features.shape}")
    print(f"test_features: {test_features.shape}")

    return train_features, test_features, data_source


if __name__ == "__main__":
    from pathlib import Path

    BASE_DIR = Path("/content/AutoLift_DM_Materials")
    OUTPUT_DIR = BASE_DIR / "02_processed_data"

    build_features(
        base_dir=BASE_DIR / "01_raw_x5_dataset",
        train_file="uplift_train.csv.gz",
        clients_file="clients.csv.gz",
        purchases_file="purchases.csv.gz",
        output_dir=OUTPUT_DIR,
    )