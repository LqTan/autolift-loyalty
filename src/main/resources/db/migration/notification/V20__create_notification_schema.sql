CREATE SCHEMA IF NOT EXISTS notification;

CREATE TABLE notification.notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type VARCHAR(100) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    recipient VARCHAR(255),
    subject VARCHAR(500),
    body TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payload JSONB,
    error_message TEXT,
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_event_type ON notification.notifications(event_type);
CREATE INDEX idx_notifications_status ON notification.notifications(status);
CREATE INDEX idx_notifications_created_at ON notification.notifications(created_at DESC);
CREATE INDEX idx_notifications_recipient ON notification.notifications(recipient);