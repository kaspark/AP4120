--liquibase formatted sql

--changeset ite4120:tea-02-category
--comment Tea classification
/* The lookup the tea table points at: Black, Green, and so on. */
create table tea.category (
    id                  bigint default nextval('core.seq_id') not null,
    name                text not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint category_pk primary key (id)
);

select core.create_table_metadata('tea.category');

create unique index category_name_ukey
    on tea.category (lower(name)) where (sys_status = 'A');
--

--changeset ite4120:tea-02-category-seed
--comment Seed the tea classifications
insert into tea.category (name) values
    ('Black'),
    ('Green'),
    ('Herbal'),
    ('Oolong');
--
