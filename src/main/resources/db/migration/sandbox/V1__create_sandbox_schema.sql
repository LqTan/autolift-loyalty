CREATE SCHEMA IF NOT EXISTS sandbox;

CREATE TABLE sandbox.sandboxes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sandboxes_name ON sandbox.sandboxes(name);