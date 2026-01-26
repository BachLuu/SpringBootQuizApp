-- liquibase formatted sql

-- changeset luubach:1768893261417-1
CREATE TABLE question_options (id UUID NOT NULL, question_id UUID NOT NULL, content TEXT NOT NULL, order_index INTEGER NOT NULL, is_correct BOOLEAN NOT NULL, is_active BOOLEAN NOT NULL, CONSTRAINT pk_question_options PRIMARY KEY (id));

-- changeset luubach:1768893261417-2
ALTER TABLE question_options ADD CONSTRAINT FK_QUESTION_OPTIONS_ON_QUESTION FOREIGN KEY (question_id) REFERENCES questions (id);

