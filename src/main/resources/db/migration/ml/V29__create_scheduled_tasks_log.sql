CREATE SCHEMA IF NOT EXISTS ml;

CREATE TABLE ml.scheduled_tasks_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    error_message TEXT
);

CREATE INDEX idx_scheduled_tasks_log_task_name ON ml.scheduled_tasks_log(task_name);
CREATE INDEX idx_scheduled_tasks_log_status ON ml.scheduled_tasks_log(status);
CREATE INDEX idx_scheduled_tasks_log_started_at ON ml.scheduled_tasks_log(started_at);