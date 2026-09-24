--liquibase formatted sql

--changeset ite4120:tea-03-tea
--comment One stocked tea: brand, classification, owner, dates, quantity
/* category_id and owner_id are foreign keys. Dates and quantity are also
   checked in the service so a violation is a clear 400, not a raw constraint
   error. */
create table tea.tea (
    id                  bigint default nextval('core.seq_id') not null,
    name                text not null,
    brand               text not null,
    category_id         bigint not null,
    owner_id            bigint not null,
    purchase_date       date not null,
    expiry_date         date not null,
    quantity            int not null,
    unit                text not null,
    sys_status          char(1),
    sys_version         int,
    sys_created_at      timestamptz,
    sys_created_by      text,
    sys_modified_at     timestamptz,
    sys_modified_by     text,
    constraint tea_pk primary key (id),
    constraint tea_category_fk foreign key (category_id) references tea.category (id),
    constraint tea_owner_fk foreign key (owner_id) references tea.tea_user (id),
    constraint tea_quantity_chk check (quantity >= 0),
    constraint tea_dates_chk check (expiry_date >= purchase_date)
);

select core.create_table_metadata('tea.tea');

create index tea_category_idx on tea.tea (category_id) where (sys_status = 'A');
create index tea_owner_idx on tea.tea (owner_id) where (sys_status = 'A');
--
