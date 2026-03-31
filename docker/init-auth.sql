-- Auth Schema Initialization
CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE IF NOT EXISTS auth.roles (
    id SMALLSERIAL PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

INSERT INTO auth.roles (role_code, description) VALUES
    ('CUSTOMER', 'Customer role for end users'),
    ('ADMIN', 'Administrator role for system management')
ON CONFLICT (role_code) DO NOTHING;
