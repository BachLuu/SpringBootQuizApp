

-- liquibase formatted sql

-- liquibase formatted sql

-- changeset Luu_Bach:1762341572453-3
ALTER TABLE users ADD new_co_string varchar(255);

-- changeset Luu_Bach:1762341572453-1
DECLARE @sql [nvarchar](MAX)
SELECT @sql = N'ALTER TABLE user_roles DROP CONSTRAINT ' + QUOTENAME([kc].[name]) FROM [sys].[key_constraints] AS [kc] WHERE [kc].[parent_object_id] = OBJECT_ID(N'user_roles') AND [kc].[type] = 'PK'
EXEC sp_executesql @sql;

-- changeset Luu_Bach:1762341572453-2
ALTER TABLE user_roles ADD CONSTRAINT user_rolesPK PRIMARY KEY (user_id, role_id);

