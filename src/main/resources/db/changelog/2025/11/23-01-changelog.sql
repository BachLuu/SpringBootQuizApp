-- liquibase formatted sql

-- changeset Luu_Bach:1763912403500-1
ALTER TABLE jhi_user_authority DROP CONSTRAINT fk_authority_name;

-- changeset Luu_Bach:1763912403500-2
ALTER TABLE jhi_user_authority DROP CONSTRAINT fk_user_id;

-- changeset Luu_Bach:1763912403500-5
DROP TABLE jhi_authority;

-- changeset Luu_Bach:1763912403500-6
DROP TABLE jhi_user;

-- changeset Luu_Bach:1763912403500-7
DROP TABLE jhi_user_authority;

-- changeset Luu_Bach:1763912403500-8
DROP SEQUENCE sequence_generator;

