CREATE TABLE credentials (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL REFERENCES profiles(id),
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    issuer VARCHAR(100),
    license_number VARCHAR(50),
    expiry_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credential_profile_id ON credentials(profile_id);
CREATE INDEX idx_credential_status ON credentials(status);
CREATE INDEX idx_credential_expiry_date ON credentials(expiry_date);