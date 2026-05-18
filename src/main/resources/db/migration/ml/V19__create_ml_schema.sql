CREATE SCHEMA IF NOT EXISTS ml;

CREATE TABLE ml.ml_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_type VARCHAR(50) NOT NULL,
    campaign_id VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    model_version VARCHAR(100),
    input_params JSONB,
    result_path VARCHAR(500),
    error_message TEXT,
    uplift_score_job_id UUID REFERENCES ml.ml_jobs(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_ml_jobs_status ON ml.ml_jobs(status);
CREATE INDEX idx_ml_jobs_campaign ON ml.ml_jobs(campaign_id);
CREATE INDEX idx_ml_jobs_type_status ON ml.ml_jobs(job_type, status);
CREATE INDEX idx_ml_jobs_uplift_dep ON ml.ml_jobs(uplift_score_job_id) WHERE uplift_score_job_id IS NOT NULL;