-- liquibase formatted sql

-- changeset luubach:1768502785568-2
CREATE TABLE quiz_sessions
(
    id                     UUID        NOT NULL,
    user_id                UUID        NOT NULL,
    quiz_id                UUID        NOT NULL,
    status                 VARCHAR(20) NOT NULL,
    created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    started_at             TIMESTAMP WITHOUT TIME ZONE,
    finished_at            TIMESTAMP WITHOUT TIME ZONE,
    expires_at             TIMESTAMP WITHOUT TIME ZONE,
    time_spent_seconds     INTEGER,
    correct_answers        INTEGER,
    total_questions        INTEGER,
    answered_questions     INTEGER,
    score                  DECIMAL(5, 2),
    points_earned          DECIMAL(10, 2),
    max_points             DECIMAL(10, 2),
    is_passed              BOOLEAN,
    current_question_index INTEGER,
    CONSTRAINT pk_quiz_sessions PRIMARY KEY (id)
);

-- changeset luubach:1768502785568-3
CREATE TABLE session_answers
(
    id                 UUID NOT NULL,
    quiz_session_id    UUID NOT NULL,
    question_id        UUID NOT NULL,
    answer_id          UUID,
    text_response      TEXT,
    is_correct         BOOLEAN,
    points_awarded     DECIMAL(10, 2),
    answered_at        TIMESTAMP WITHOUT TIME ZONE,
    time_spent_seconds INTEGER,
    answer_order       INTEGER,
    is_reviewed        BOOLEAN,
    reviewer_feedback  TEXT,
    CONSTRAINT pk_session_answers PRIMARY KEY (id)
);

-- changeset luubach:1768502785568-4
ALTER TABLE quiz_sessions
    ADD CONSTRAINT FK_QUIZ_SESSIONS_ON_QUIZ FOREIGN KEY (quiz_id) REFERENCES quizzes (id);

-- changeset luubach:1768502785568-5
ALTER TABLE quiz_sessions
    ADD CONSTRAINT FK_QUIZ_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset luubach:1768502785568-6
ALTER TABLE session_answers
    ADD CONSTRAINT FK_SESSION_ANSWERS_ON_ANSWER FOREIGN KEY (answer_id) REFERENCES answers (id);

-- changeset luubach:1768502785568-7
ALTER TABLE session_answers
    ADD CONSTRAINT FK_SESSION_ANSWERS_ON_QUESTION FOREIGN KEY (question_id) REFERENCES questions (id);

-- changeset luubach:1768502785568-8
ALTER TABLE session_answers
    ADD CONSTRAINT FK_SESSION_ANSWERS_ON_QUIZ_SESSION FOREIGN KEY (quiz_session_id) REFERENCES quiz_sessions (id);

-- changeset luubach:1768502785568-1
ALTER TABLE refresh_tokens
    ALTER COLUMN token SET NOT NULL;

