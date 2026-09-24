--liquibase formatted sql

--changeset ite4120:tea-01-user
--comment People who own tea in the register
/* Business table: id from core.seq_id, sys columns filled by the platform
   trigger. email is an identifier, stored as text. */
create table tea.tea_user (
    id                  bigint default nextval('core.seq_id') not null,
    name                text not null,
    email               text not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint tea_user_pk primary key (id)
);

select core.create_table_metadata('tea.tea_user');

/* Unique among active rows only, so a retired address may be used again. */
create unique index tea_user_email_ukey
    on tea.tea_user (lower(email)) where (sys_status = 'A');
--
