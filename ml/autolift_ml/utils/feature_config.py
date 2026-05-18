"""Feature definitions for uplift modeling."""
from dataclasses import dataclass
from typing import Optional


@dataclass
class FeatureConfig:
    """Configuration for a single feature."""

    name: str
    source_column: str
    description: str
    used_for: str  # "uplift", "gp", or "uplift+gp"


PURCHASE_FEATURES = [
    FeatureConfig(
        name="recency_days",
        source_column="recency_days",
        description="Days since most recent purchase",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="frequency_total",
        source_column="frequency_total",
        description="Number of unique transactions",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="monetary_total",
        source_column="monetary_total",
        description="Total purchase amount",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="avg_basket_value",
        source_column="avg_basket_value",
        description="Average transaction value",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="total_quantity",
        source_column="total_quantity",
        description="Total items purchased",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="unique_product_count",
        source_column="unique_product_count",
        description="Number of unique products",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="unique_category_count",
        source_column="unique_category_count",
        description="Number of unique categories",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="purchase_rows",
        source_column="purchase_rows",
        description="Raw rows before aggregation",
        used_for="uplift+gp",
    ),
]

CLIENT_FEATURES = [
    FeatureConfig(
        name="age",
        source_column="age",
        description="Customer age",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="gender",
        source_column="gender",
        description="Encoded gender",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="first_issue_date",
        source_column="first_issue_date",
        description="Days since first issue",
        used_for="uplift+gp",
    ),
    FeatureConfig(
        name="first_redeem_date",
        source_column="first_redeem_date",
        description="Days since first redeem",
        used_for="uplift+gp",
    ),
]

ALL_FEATURES = PURCHASE_FEATURES + CLIENT_FEATURES


def get_feature_names() -> list[str]:
    """Get list of all feature names."""
    return [f.name for f in ALL_FEATURES]


def get_required_columns() -> list[str]:
    """Get list of required source columns."""
    return ["customer_id", "treatment_flg", "target"] + [f.source_column for f in ALL_FEATURES]