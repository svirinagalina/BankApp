CREATE TABLE IF NOT EXISTS audit_events (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    event_type VARCHAR(255),
    occurred_at TIMESTAMP,
    source VARCHAR(255)
);
