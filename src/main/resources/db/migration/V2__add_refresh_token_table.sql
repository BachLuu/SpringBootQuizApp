create table refresh_tokens (
    id uniqueidentifier not null,
    expiry_date datetimeoffset(6) not null,
    token varchar(255) not null,
    user_id uniqueidentifier,
    primary key (id)
);

alter table refresh_tokens
   add constraint UQ_refresh_tokens_token unique (token);

alter table refresh_tokens
   add constraint FK_refresh_tokens_user foreign key (user_id)
   references users(id);
