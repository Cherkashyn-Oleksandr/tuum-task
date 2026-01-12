CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    country VARCHAR(2) NOT NULL,
    created_at TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS balance (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    UNIQUE (account_id, currency)
    );

CREATE TABLE IF NOT EXISTS account_transaction (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    direction VARCHAR(3) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
    );
