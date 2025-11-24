-- liquibase formatted sql

-- changeset Luu_Bach:1763993645901-3
ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_user UNIQUE (user_id);

-- changeset Luu_Bach:1763993645901-1
ALTER TABLE users ALTER COLUMN is_active bit NULL;

-- changeset Luu_Bach:1763993645901-2
ALTER TABLE refresh_tokens ALTER COLUMN token varchar(500);

