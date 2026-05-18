"""Train GP rules to explain Persuadable segment."""
import random
import operator
import numpy as np
import pandas as pd
from pathlib import Path
from deap import base, creator, tools, algorithms


TERMINALS = [
    ("recency_days", lambda c: c < 14),
    ("frequency_total", lambda c: c > 5),
    ("monetary_total", lambda c: c > 50000),
    ("avg_basket_value", lambda c: c > 10000),
    ("total_quantity", lambda c: c > 50),
    ("unique_product_count", lambda c: c > 20),
    ("unique_category_count", lambda c: c > 3),
]

COMPARATORS = [
    (operator.lt, "<"),
    (operator.gt, ">"),
]

AND_EXPRESSION = "({} AND {})"
OR_EXPRESSION = "({} OR {})"
TERMINAL_EXPRESSION = "({} {} {})"


def encode_rule(rule_parts: list, rule_str: str, context: dict) -> str:
    return rule_str


class GPModel:
    def __init__(self, df: pd.DataFrame, feature_cols: list):
        self.df = df
        self.feature_cols = feature_cols
        self.rules = []

    def evaluate_rule(self, individual) -> tuple:
        rule_str = self._individual_to_str(individual)
        hits = self.apply_rule(rule_str)
        if hits.sum() == 0:
            return 0.0, 0.0

        true_positives = ((hits == 1) & (self.df["target_flag"] == 1)).sum()
        predicted_positives = hits.sum()
        actual_positives = self.df["target_flag"].sum()

        precision = true_positives / predicted_positives if predicted_positives > 0 else 0
        recall = true_positives / actual_positives if actual_positives > 0 else 0
        f1 = 2 * precision * recall / (precision + recall) if (precision + recall) > 0 else 0

        return f1, precision

    def apply_rule(self, rule_str: str) -> pd.Series:
        condition = self._parse_rule(rule_str)
        return condition

    def _parse_rule(self, rule_str: str) -> pd.Series:
        df = self.df
        result = pd.Series([True] * len(df))

        parts = rule_str.split(" AND ")
        for part in parts:
            part = part.strip()
            for feat_name, threshold_gen in TERMINALS:
                if feat_name in part:
                    for comp_op, comp_sym in COMPARATORS:
                        if comp_sym in part:
                            try:
                                threshold_part = part.split(comp_sym)[1].strip()
                                threshold = float(threshold_part)
                                if comp_op == operator.lt:
                                    result = result & (df[feat_name] < threshold)
                                elif comp_op == operator.gt:
                                    result = result & (df[feat_name] > threshold)
                                break
                            except (ValueError, IndexError):
                                pass
                    break
        return result

    def _individual_to_str(self, individual) -> str:
        nodes = [self._node_to_str(node) for node in individual]
        return " AND ".join([n for n in nodes if n])

    def _node_to_str(self, node) -> str:
        if isinstance(node, tuple) and len(node) == 2:
            feat_name, _ = node
            for feat_name_check, threshold_gen in TERMINALS:
                if feat_name_check == feat_name:
                    return f"{feat_name} > 0"
        return str(node)


def build_gp_model(
    train_df: pd.DataFrame,
    feature_cols: list,
    output_dir: Path,
    population_size: int = 200,
    generations: int = 20,
    crossover_prob: float = 0.7,
    mutation_prob: float = 0.2,
    top_k: int = 10,
) -> list:
    creator.create("FitnessMax", base.Fitness, weights=(1.0,))
    creator.create("Individual", list, fitness=creator.FitnessMax)

    toolbox = base.Toolbox()

    def gen_terminal():
        feat_name, _ = random.choice(TERMINALS)
        comp_op, comp_sym = random.choice(COMPARATORS)
        threshold = random.uniform(0, 100)
        return (feat_name, comp_op, threshold, comp_sym)

    toolbox.register("individual", lambda: creator.Individual([gen_terminal() for _ in range(3)]))
    toolbox.register("population", tools.initRepeat, list, toolbox.individual)
    toolbox.register("mate", tools.cxOnePoint)
    toolbox.register("mutate", lambda ind: (creator.Individual([gen_terminal() for _ in ind]),))
    toolbox.register("select", tools.selTournament, tournsize=3)

    gp_model = GPModel(train_df, feature_cols)

    def eval_with_model(ind):
        return gp_model.evaluate_rule(ind)[0],

    toolbox.register("evaluate", eval_with_model)

    pop = toolbox.population(n=population_size)
    algorithms.eaSimple(pop, toolbox, cxpb=crossover_prob, mutpb=mutation_prob, ngen=generations, verbose=False)

    top_individuals = tools.selBest(pop, k=top_k)

    rules = []
    for idx, ind in enumerate(top_individuals):
        rule_str = ind_to_rule_str(ind)
        fitness, precision = gp_model.evaluate_rule(ind)
        recall = gp_model.evaluate_rule(ind)[1]

        rule = {
            "rule_text": rule_str,
            "rule_expression": rule_str,
            "precision": precision,
            "recall": recall,
            "f1": fitness,
        }
        rules.append(rule)

    return rules


def ind_to_rule_str(individual) -> str:
    parts = []
    for node in individual:
        if isinstance(node, tuple) and len(node) == 4:
            feat_name, _, threshold, comp_sym = node
            parts.append(f"{feat_name} {comp_sym} {threshold:.2f}")
    return " AND ".join(parts)


def train_gp_rules(
    gp_input_path: Path,
    output_dir: Path,
    feature_cols: list = None,
    population_size: int = 200,
    generations: int = 20,
) -> pd.DataFrame:
    df = pd.read_csv(gp_input_path)

    if feature_cols is None:
        feature_cols = [
            "recency_days", "frequency_total", "monetary_total",
            "avg_basket_value", "total_quantity",
            "unique_product_count", "unique_category_count",
        ]

    for col in feature_cols:
        if col in df.columns:
            df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0)

    df["target_flag"] = pd.to_numeric(df["target_flag"], errors="coerce").fillna(0).astype(int)

    rules = build_gp_model(
        train_df=df,
        feature_cols=feature_cols,
        output_dir=output_dir,
        population_size=population_size,
        generations=generations,
    )

    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    gp_df = df.copy()
    gp_df["campaign_id"] = gp_df.get("campaign_id_gp", "x5-campaign-v1")

    return gp_df, rules


if __name__ == "__main__":
    from pathlib import Path

    BASE_DIR = Path("/home/archer/Projects/java_projects/autolift-loyalty/ml")
    ARTIFACTS_DIR = BASE_DIR / "artifacts" / "outputs"

    gp_df, rules = train_gp_rules(
        gp_input_path=ARTIFACTS_DIR / "gp_input.csv",
        output_dir=ARTIFACTS_DIR,
        population_size=200,
        generations=20,
    )

    print(f"Trained {len(rules)} GP rules")
    for r in rules:
        print(f"  {r['rule_text']}: f1={r['f1']:.3f}, precision={r['precision']:.3f}, recall={r['recall']:.3f}")