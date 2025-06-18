CREATE TABLE auth_login_attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    success BOOLEAN NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT
);

CREATE INDEX idx_auth_login_attempts_user_id ON auth_login_attempts(user_id);
CREATE INDEX idx_auth_login_attempts_timestamp ON auth_login_attempts(timestamp);
CREATE INDEX idx_auth_login_attempts_success ON auth_login_attempts(success);