"""Export X5 clients data as customers_for_import.csv for Spring Boot import."""
import hashlib
import uuid
import argparse
import sys
from datetime import datetime
from pathlib import Path

import pandas as pd

sys.path.insert(0, str(Path(__file__).parent.parent))

from autolift_ml.utils.data_loader import find_csv_by_names, safe_read_csv


def customer_id_to_uuid(customer_id: str) -> str:
    """Convert X5 customer_id (hex string) to UUID v5."""
    ns_uuid = uuid.UUID("6ba7b810-9dad-11d1-80b4-00c04fd430c8")
    return str(uuid.uuid5(ns_uuid, str(customer_id)))


def export_customers(
    data_dir: Path,
    output_dir: Path,
    force: bool = False,
) -> Path:
    """Export X5 clients.csv.gz as customers_for_import.csv.

    Args:
        data_dir: Directory containing X5 dataset files
        output_dir: Directory for output CSV
        force: Overwrite existing file

    Returns:
        Path to output CSV file
    """
    data_dir = Path(data_dir)
    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    output_file = output_dir / "customers_for_import.csv"

    if output_file.exists() and not force:
        print(f"Output file already exists: {output_file}")
        print("Use --force to overwrite")
        return output_file

    clients_path = find_csv_by_names(
        [data_dir],
        ["clients.csv", "clients"],
    )

    if clients_path is None:
        raise FileNotFoundError(f"Cannot find clients.csv in {data_dir}")

    print(f"Loading clients from: {clients_path}")
    clients_df = safe_read_csv(clients_path)
    print(f"Loaded {len(clients_df)} clients")

    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    customers = []
    for _, row in clients_df.iterrows():
        cid = str(row["customer_id"])
        customers.append({
            "id": customer_id_to_uuid(cid),
            "customer_id": cid,
            "name": f"X5 Customer {cid[:8]}",
            "email": f"{cid}@x5.client",
            "phone": "",
            "segment": "NORMAL",
            "status": "ACTIVE",
            "created_at": now,
            "updated_at": now,
        })

    result_df = pd.DataFrame(customers)

    result_df.to_csv(output_file, index=False)
    print(f"Exported {len(result_df)} customers to: {output_file}")

    print("\nSample rows:")
    print(result_df.head(3).to_string())

    return output_file


def main():
    parser = argparse.ArgumentParser(description="Export X5 clients as Spring Boot customer import CSV")
    parser.add_argument(
        "--data-dir", "-d",
        type=str,
        default="data",
        help="Directory containing X5 dataset (default: data)"
    )
    parser.add_argument(
        "--output-dir", "-o",
        type=str,
        default="artifacts/outputs",
        help="Directory for output CSV (default: artifacts/outputs)"
    )
    parser.add_argument(
        "--force", "-f",
        action="store_true",
        help="Force overwrite existing output file"
    )

    args = parser.parse_args()

    ml_dir = Path(__file__).parent.parent
    data_dir = ml_dir / args.data_dir if not Path(args.data_dir).is_absolute() else Path(args.data_dir)
    output_dir = ml_dir / args.output_dir if not Path(args.output_dir).is_absolute() else Path(args.output_dir)

    try:
        output_file = export_customers(
            data_dir=data_dir,
            output_dir=output_dir,
            force=args.force,
        )
        print(f"\nExport succeeded: {output_file}")
        sys.exit(0)
    except Exception as e:
        print(f"\nExport failed: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()