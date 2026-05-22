ALTER TABLE ml.ml_jobs ADD COLUMN metrics JSONB;

COMMENT ON COLUMN ml.ml_jobs.metrics IS 'Stores uplift modeling metrics: uplift_at_10, uplift_at_20, uplift_at_30, auuc, qini_auc, etc.';