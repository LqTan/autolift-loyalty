"""Main pipeline: Download X5 data -> Build features -> Train -> Evaluate -> Export scores."""
import argparse
import sys
import urllib.request
import zipfile
from datetime import datetime
from pathlib import Path

import pandas as pd
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent))

from autolift_ml.pipeline.build_features import build_features
from autolift_ml.pipeline.train import train_t_learner, predict_uplift
from autolift_ml.pipeline.evaluate import evaluate_model
from autolift_ml.pipeline.export import export_uplift_scores, export_feature_snapshots
from autolift_ml.utils.metrics import print_evaluation_report


X5_DATASET_URL = "https://ods.ai/api/competitions/x5-retailhero-uplift-modeling/datasets"
ODS_API_URL = "https://ods.ai/api/v2"


X5_FILES = {
    "uplift_train.csv.gz": {
        "url": "https://sklift.s3.eu-west-2.amazonaws.com/uplift_train.csv.gz",
        "md5": "2720bbb659daa9e0989b2777b6a42d19",
    },
    "clients.csv.gz": {
        "url": "https://sklift.s3.eu-west-2.amazonaws.com/clients.csv.gz",
        "md5": "b9cdeb2806b732771de03e819b3354c5",
    },
    "purchases.csv.gz": {
        "url": "https://sklift.s3.eu-west-2.amazonaws.com/purchases.csv.gz",
        "md5": "48d2de13428e24e8b61d66fef02957a8",
    },
}


def download_with_progress(url: str, dest_path: Path, desc: str = "Downloading") -> bool:
    """Download file with wget for faster multi-connection download."""
    try:
        print(f"{desc}: {url}")
        print(f"Destination: {dest_path}")

        import subprocess
        result = subprocess.run(
            ["wget", "-c", "--progress=bar:force:noscroll", "-O", str(dest_path), url],
            capture_output=False,
            text=True,
        )

        if result.returncode == 0:
            print(f"Download complete: {dest_path}")
            return True
        else:
            print(f"Download failed with code: {result.returncode}")
            return False
    except FileNotFoundError:
        print("wget not found, falling back to urllib...")
        try:
            import urllib.request
            print(f"{desc}: {url}")
            print(f"Destination: {dest_path}")

            def report_hook(block_num, block_size, total_size):
                downloaded = block_num * block_size
                percent = min(100, downloaded * 100 // total_size) if total_size > 0 else 0
                bar_len = 40
                filled = int(bar_len * percent / 100)
                bar = "=" * filled + "-" * (bar_len - filled)
                print(f"\r{desc}: [{bar}] {percent}% ({downloaded/1024/1024:.1f}MB)", end="", flush=True)
                if percent >= 100:
                    print()

            urllib.request.urlretrieve(url, dest_path, reporthook=report_hook)
            return True
        except Exception as e:
            print(f"\nDownload failed: {e}")
            return False
    except Exception as e:
        print(f"\nDownload failed: {e}")
        return False


def download_x5_dataset(data_dir: Path, force: bool = False) -> dict:
    """Download X5 RetailHero dataset from sklift.s3.eu-west-2.amazonaws.com.

    Returns dict with paths to downloaded files.
    """
    data_dir = Path(data_dir)
    data_dir.mkdir(parents=True, exist_ok=True)

    train_file = data_dir / "uplift_train.csv.gz"
    clients_file = data_dir / "clients.csv.gz"
    purchases_file = data_dir / "purchases.csv.gz"

    if all(f.exists() for f in [train_file, clients_file, purchases_file]) and not force:
        print("X5 dataset already exists, skipping download.")
        return {
            "train": train_file,
            "clients": clients_file,
            "purchases": purchases_file,
        }

    print("=" * 60)
    print("X5 RETAILHERO DATASET DOWNLOAD")
    print("=" * 60)

    import hashlib

    def file_md5(path: Path, chunk_size: int = 1024 * 1024) -> str:
        md5 = hashlib.md5()
        with path.open("rb") as file:
            for chunk in iter(lambda: file.read(chunk_size), b""):
                md5.update(chunk)
        return md5.hexdigest()

    for filename, meta in X5_FILES.items():
        output_path = data_dir / filename

        should_download = True

        if output_path.exists():
            current_md5 = file_md5(output_path)
            if current_md5 == meta["md5"]:
                should_download = False
                print(f"OK: {filename}")
            else:
                print(f"MD5 mismatch. Re-downloading: {filename}")
                output_path.unlink()

        if should_download:
            print(f"Downloading: {filename} from {meta['url']}")
            success = download_with_progress(meta["url"], output_path, desc=filename)
            if not success or not output_path.exists():
                print(f"Download failed for {filename}")
                return {}

            downloaded_md5 = file_md5(output_path)
            if downloaded_md5 != meta["md5"]:
                print(f"MD5 check failed for {filename}. Expected {meta['md5']}, got {downloaded_md5}.")
                return {}

            print(f"Downloaded and verified: {filename}")

    print("Required X5 files are ready:", data_dir)

    return {
        "train": train_file,
        "clients": clients_file,
        "purchases": purchases_file,
    }


def run_pipeline(
    data_dir: Path,
    output_dir: Path,
    model_dir: Path,
    config_path: Path = None,
    download: bool = True,
    force_redownload: bool = False,
) -> dict:
    """Run the complete ML pipeline.

    Args:
        data_dir: Directory containing raw X5 data
        output_dir: Directory for processed features and outputs
        model_dir: Directory for saved models
        config_path: Path to config.yaml (optional)
        download: Whether to attempt downloading X5 dataset
        force_redownload: Force re-download even if files exist

    Returns:
        Dictionary with pipeline results and file paths
    """
    start_time = datetime.now()

    data_dir = Path(data_dir)
    output_dir = Path(output_dir)
    model_dir = Path(model_dir)

    for d in [data_dir, output_dir, model_dir]:
        d.mkdir(parents=True, exist_ok=True)

    config = {}
    if config_path and config_path.exists():
        with open(config_path) as f:
            config = yaml.safe_load(f)
    else:
        config_path = Path("ml/config.yaml")
        if config_path.exists():
            with open(config_path) as f:
                config = yaml.safe_load(f)

    print("=" * 60)
    print("AUTOLIFT UPLIFT MODELING PIPELINE")
    print("=" * 60)
    print(f"\nConfig: {config.get('dataset', {}).get('name', 'unknown')}")
    print(f"Start time: {start_time.isoformat()}")

    if download:
        files = download_x5_dataset(data_dir, force=force_redownload)
        if not files:
            print("ERROR: X5 dataset not found and download unavailable.")
            print("Please download manually and place in:", data_dir)
            return {}
    else:
        files = {
            "train": data_dir / config.get("dataset", {}).get("train_file", "uplift_train.csv.gz"),
            "clients": data_dir / config.get("dataset", {}).get("clients_file", "clients.csv.gz"),
            "purchases": data_dir / config.get("dataset", {}).get("purchases_file", "purchases.csv.gz"),
        }

    print("\n" + "=" * 60)
    print("STEP 1: BUILD FEATURES")
    print("=" * 60)

    from autolift_ml.pipeline.build_features import build_features

    chunksize = config.get("features", {}).get("chunksize", 300_000)

    train_features, test_features, data_source = build_features(
        base_dir=data_dir,
        train_file=config.get("dataset", {}).get("train_file", "uplift_train.csv.gz"),
        clients_file=config.get("dataset", {}).get("clients_file", "clients.csv.gz"),
        purchases_file=config.get("dataset", {}).get("purchases_file", "purchases.csv.gz"),
        output_dir=output_dir,
        chunksize=chunksize,
    )

    print(f"\nTrain features shape: {train_features.shape}")
    print(f"Test features shape: {test_features.shape}")

    print("\n" + "=" * 60)
    print("STEP 2: TRAIN T-LEARNER MODEL")
    print("=" * 60)

    feature_cols = [
        col for col in train_features.columns
        if col not in ["customer_id", "treatment_flg", "target"]
    ]

    model_info = train_t_learner(
        train_features=train_features,
        model_dir=model_dir,
        base_model=config.get("model", {}).get("base_model", "RandomForestClassifier"),
        n_estimators=config.get("model", {}).get("n_estimators", 100),
        random_state=config.get("model", {}).get("random_state", 42),
    )

    print("\n" + "=" * 60)
    print("STEP 3: EVALUATE MODEL")
    print("=" * 60)

    import joblib
    model_treatment = joblib.load(model_info["model_treatment_path"])
    model_control = joblib.load(model_info["model_control_path"])

    X = train_features[feature_cols].values
    p_treatment, p_control, uplift_score = predict_uplift(
        model_treatment, model_control, X
    )

    result = train_features.copy()
    result["p_treatment"] = p_treatment
    result["p_control"] = p_control
    result["uplift_score"] = uplift_score

    top_k_rates = config.get("evaluation", {}).get("top_k_rates", [0.10, 0.20, 0.30])

    from autolift_ml.utils.metrics import print_evaluation_report
    print_evaluation_report(result, top_k_rates=top_k_rates)

    print("\n" + "=" * 60)
    print("STEP 4: EXPORT SCORES FOR SPRING BOOT")
    print("=" * 60)

    test_df = test_features.copy()

    model_version = config.get("output", {}).get("model_version", "v1")

    uplift_csv = export_uplift_scores(
        test_features=test_df,
        model_info=model_info,
        model_version=model_version,
        output_dir=output_dir,
        campaign_id="x5-campaign-v1",
    )

    feature_csv = export_feature_snapshots(
        test_features=test_df,
        model_version=model_version,
        output_dir=output_dir,
        campaign_id="x5-campaign-v1",
    )

    end_time = datetime.now()
    duration = end_time - start_time

    print("\n" + "=" * 60)
    print("PIPELINE COMPLETE")
    print("=" * 60)
    print(f"Duration: {duration}")
    print(f"Start: {start_time.isoformat()}")
    print(f"End: {end_time.isoformat()}")
    print(f"\nOutput files:")
    print(f"  - Uplift scores: {uplift_csv}")
    print(f"  - Feature snapshots: {feature_csv}")
    print(f"  - Models: {model_dir}")
    print("\n" + "=" * 60)

    return {
        "uplift_scores": str(uplift_csv),
        "feature_snapshots": str(feature_csv),
        "model_dir": str(model_dir),
        "duration": str(duration),
        "n_samples": len(test_df),
    }


def main():
    parser = argparse.ArgumentParser(description="Autolift Uplift Modeling Pipeline")
    parser.add_argument(
        "--data-dir", "-d",
        type=str,
        default="data",
        help="Directory for raw X5 data (default: data)"
    )
    parser.add_argument(
        "--output-dir", "-o",
        type=str,
        default="artifacts/outputs",
        help="Directory for outputs (default: artifacts/outputs)"
    )
    parser.add_argument(
        "--model-dir", "-m",
        type=str,
        default="artifacts/models",
        help="Directory for saved models (default: artifacts/models)"
    )
    parser.add_argument(
        "--config", "-c",
        type=str,
        default="config.yaml",
        help="Path to config.yaml (default: config.yaml)"
    )
    parser.add_argument(
        "--no-download",
        action="store_true",
        help="Skip dataset download, use existing files"
    )
    parser.add_argument(
        "--force-download",
        action="store_true",
        help="Force re-download even if files exist"
    )

    args = parser.parse_args()

    ml_dir = Path(__file__).parent.parent
    data_dir = ml_dir / args.data_dir if not Path(args.data_dir).is_absolute() else Path(args.data_dir)
    output_dir = ml_dir / args.output_dir if not Path(args.output_dir).is_absolute() else Path(args.output_dir)
    model_dir = ml_dir / args.model_dir if not Path(args.model_dir).is_absolute() else Path(args.model_dir)
    config_path = ml_dir / args.config if not Path(args.config).is_absolute() else Path(args.config)

    result = run_pipeline(
        data_dir=data_dir,
        output_dir=output_dir,
        model_dir=model_dir,
        config_path=config_path,
        download=not args.no_download,
        force_redownload=args.force_download,
    )

    if result:
        print("\nPipeline succeeded!")
        sys.exit(0)
    else:
        print("\nPipeline failed!")
        sys.exit(1)


if __name__ == "__main__":
    main()
