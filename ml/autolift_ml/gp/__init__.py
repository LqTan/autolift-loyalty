"""GP package for Genetic Programming rule extraction."""
from .build_gp_input import build_gp_input
from .train_gp_rules import train_gp_rules

__all__ = ["build_gp_input", "train_gp_rules"]