"""Data loading utilities for X5 RetailHero dataset."""
from pathlib import Path
from typing import Optional

import numpy as np
import pandas as pd


def normalize_id_column(df: pd.DataFrame) -> pd.DataFrame:
    """Normalize customer ID column name to 'customer_id'."""
    df = df.copy()
    candidates = ["customer_id", "client_id", "id", "ID"]

    found = None
    for col in candidates:
        if col in df.columns:
            found = col
            break

    if found and found != "customer_id":
        df = df.rename(columns={found: "customer_id"})

    return df


def infer_column(df: pd.DataFrame, candidates: list[str]) -> Optional[str]:
    """Infer column name from candidates list."""
    cols_lower = {c.lower(): c for c in df.columns}

    for c in candidates:
        if c.lower() in cols_lower:
            return cols_lower[c.lower()]

    return None


def safe_read_csv(path: Optional[Path], nrows: Optional[int] = None) -> Optional[pd.DataFrame]:
    """Read CSV with ID column normalization."""
    if path is None:
        return None

    df = pd.read_csv(path, nrows=nrows)
    return normalize_id_column(df)


def encode_non_numeric(
    df: pd.DataFrame,
    exclude: tuple = ("customer_id", "target", "treatment_flg")
) -> pd.DataFrame:
    """Encode non-numeric columns as category codes."""
    df = df.copy()

    for col in df.columns:
        if col in exclude:
            continue

        if df[col].dtype == "object" or str(df[col].dtype).startswith("category"):
            df[col] = df[col].astype("category").cat.codes

    return df


def fill_numeric_na(df: pd.DataFrame) -> pd.DataFrame:
    """Fill NaN values in numeric columns with median."""
    df = df.copy()

    for col in df.columns:
        if col == "customer_id":
            continue

        if pd.api.types.is_numeric_dtype(df[col]):
            df[col] = df[col].replace([np.inf, -np.inf], np.nan)

            if df[col].notna().any():
                df[col] = df[col].fillna(df[col].median())
            else:
                df[col] = df[col].fillna(0)

    return df


def find_csv_by_names(search_roots: list[Path], candidate_names: list[str]) -> Optional[Path]:
    """Find CSV file by name in search roots."""
    candidate_names = {n.lower() for n in candidate_names}
    candidate_names_gz = {n + ".gz" for n in candidate_names}

    for root in search_roots:
        root = Path(root)
        if not root.exists():
            continue

        for pattern in ["*.csv", "*.csv.gz"]:
            for path in root.rglob(pattern):
                name = path.name.lower()
                if name in candidate_names or name in candidate_names_gz:
                    return path

    return None


def load_dataset(
    base_dir: Path,
    train_file: str,
    clients_file: str,
    purchases_file: str,
    nrows_train: Optional[int] = None,
    nrows_clients: Optional[int] = None,
) -> tuple[Optional[pd.DataFrame], Optional[pd.DataFrame], Optional[Path]]:
    """Load X5 dataset files.

    Returns:
        Tuple of (train_df, clients_df, purchases_path)
    """
    search_roots = [base_dir, Path("/content"), Path("/root")]

    train_path = find_csv_by_names(search_roots, [train_file.replace(".gz", "")])
    clients_path = find_csv_by_names(search_roots, [clients_file.replace(".gz", "")])
    purchases_path = find_csv_by_names(search_roots, [purchases_file.replace(".gz", "")])

    if train_path is None:
        raise FileNotFoundError(f"Cannot find train file: {train_file}")

    train_df = safe_read_csv(train_path, nrows=nrows_train)
    if train_df is not None:
        train_df["customer_id"] = train_df["customer_id"].astype(str)

    clients_df = safe_read_csv(clients_path, nrows=nrows_clients)
    if clients_df is not None:
        clients_df["customer_id"] = clients_df["customer_id"].astype(str)

    return train_df, clients_df, purchases_path