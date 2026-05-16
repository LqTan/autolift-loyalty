CREATE TABLE IF NOT EXISTS sandbox.event_publication (
    id UUID PRIMARY KEY,
    event_aggregate_identifier VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    event_modules VARCHAR(255) NOT NULL,
    publication_status VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    completion_date TIMESTAMP,
    error_message VARCHAR(1000),
    listener_id VARCHAR(255),
    publication_date TIMESTAMP,
    serialized_event TEXT
);

CREATE INDEX idx_event_publication_status ON sandbox.event_publication(publication_status);
CREATE INDEX idx_event_publication_aggregate ON sandbox.event_publication(event_aggregate_identifier);
CREATE INDEX idx_event_publication_completion ON sandbox.event_publication(completion_date) WHERE completion_date IS NULL;