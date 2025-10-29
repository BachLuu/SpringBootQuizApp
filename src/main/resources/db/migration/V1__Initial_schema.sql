create table questions
(
    id            uniqueidentifier not null
        primary key,
    content       text             not null,
    is_active     bit              not null,
    question_type varchar(255)     not null
        check ([question_type] = 'LongAnswer' OR [question_type] = 'ShortAnswer' OR
               [question_type] = 'FillInTheBlanks' OR [question_type] = 'TrueFalse' OR
               [question_type] = 'SingleChoice' OR [question_type] = 'MultipleChoice')
)
go

create table answers
(
    id          uniqueidentifier not null
        primary key,
    content     text             not null,
    is_active   bit              not null,
    is_correct  bit              not null,
    question_id uniqueidentifier not null
        constraint FK3erw1a3t0r78st8ty27x6v3g1
            references questions
)
go

create table quizzes
(
    id            uniqueidentifier not null
        primary key,
    description   text,
    duration      int              not null
        check ([duration] <= 3600 AND [duration] >= 1),
    is_active     bit              not null,
    thumbnail_url varchar(500),
    title         varchar(255)     not null
)
go

create table quiz_questions
(
    id          uniqueidentifier not null
        primary key,
    [order]     int,
    question_id uniqueidentifier not null
        constraint FKev41c723fx659v28pjycox15o
            references questions,
    quiz_id     uniqueidentifier not null
        constraint FKanfmgf6ksbdnv7ojb0pfve54q
            references quizzes
)
go

create table roles
(
    id          uniqueidentifier not null
        primary key,
    description varchar(50)      not null,
    is_active   bit              not null,
    name        varchar(50)      not null
        constraint UKofx66keruapi6vyqpv6f2or37
            unique
)
go

create table users
(
    id            uniqueidentifier not null
        primary key,
    avatar        varchar(500),
    created_at    datetime2(6),
    date_of_birth date,
    email         varchar(255)     not null
        constraint UK6dotkott2kjsp8vw4d0m25fb7
            unique,
    first_name    varchar(50)      not null,
    is_active     bit              not null,
    last_name     varchar(50)      not null,
    password      varchar(255)     not null
)
go

create table user_quizzes
(
    id          uniqueidentifier not null
        primary key,
    finished_at datetime2(6),
    quiz_code   varchar(100),
    quiz_id     uniqueidentifier
        constraint FKt5qv2gaye4tr50w4lc4ohx0g6
            references quizzes,
    started_at  datetime2(6),
    user_id     uniqueidentifier
        constraint FKn9d0x6rhvkcq499p2xj25n567
            references users
)
go

create table user_answers
(
    id           uniqueidentifier not null
        primary key,
    answer_id    uniqueidentifier not null
        constraint FKq9ubv2ar56hkwxokdbp72b5by
            references answers,
    is_correct   bit              not null,
    question_id  uniqueidentifier not null
        constraint FK6b46l4bb7a6wfxvmn6l7ig8vo
            references questions,
    user_quiz_id uniqueidentifier not null
        constraint FK7fyhfbm7195iohjh520xqglem
            references user_quizzes
)
go

create table user_roles
(
    user_id uniqueidentifier not null
        constraint FKhfh9dx7w3ubf1co1vdev94g3f
            references users,
    role_id uniqueidentifier not null
        constraint FKh8ciramu9cc9q3qcqiv4ue8a6
            references roles,
    primary key (user_id, role_id)
)
go

