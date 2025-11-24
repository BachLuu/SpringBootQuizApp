-- liquibase formatted sql

-- changeset Luu_Bach:1763915360990-1
ALTER TABLE users ALTER COLUMN is_active bit NULL;

-- changeset Luu_Bach:1763915360990-2
ALTER TABLE refresh_tokens ALTER COLUMN token varchar(500);

