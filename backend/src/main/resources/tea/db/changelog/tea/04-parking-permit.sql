--liquibase formatted sql

--changeset ite4120:tea-04-parking-permit
--comment Reshape the register into a parking permit portal
/* The shipped tea changesets stay as they are. Tea stock cannot become a
   permit, so those rows are removed. The people stay: they are the permit
   holders. The old classifications are replaced with permit types. */
delete from tea.tea;
delete from tea.category;

alter table tea.tea drop constraint tea_quantity_chk;
alter table tea.tea drop constraint tea_dates_chk;
alter table tea.tea drop constraint tea_category_fk;
alter table tea.tea drop constraint tea_owner_fk;

drop index tea.tea_category_idx;
drop index tea.tea_owner_idx;

alter table tea.tea drop column name;
alter table tea.tea drop column brand;
alter table tea.tea drop column purchase_date;
alter table tea.tea drop column expiry_date;
alter table tea.tea drop column quantity;
alter table tea.tea drop column unit;

alter table tea.tea_user rename to person;
alter table tea.category rename to permit_type;
alter table tea.tea rename to permits;

alter table tea.person rename constraint tea_user_pk to person_pk;
alter table tea.permit_type rename constraint category_pk to permit_type_pk;
alter table tea.permits rename constraint tea_pk to permit_pk;

alter index tea.tea_user_email_ukey rename to person_email_ukey;
alter index tea.category_name_ukey rename to permit_type_name_ukey;

alter table tea.permits rename column category_id to permit_type_id;
alter table tea.permits rename column owner_id to holder_id;

alter table tea.permits
    add column start_date date not null,
    add column end_date date not null,
    add column granted_by_id bigint not null,
    add column description text not null,
    add constraint permit_type_fk foreign key (permit_type_id) references tea.permit_type (id),
    add constraint permit_holder_fk foreign key (holder_id) references tea.person (id),
    add constraint permit_granted_by_fk foreign key (granted_by_id) references tea.person (id),
    add constraint permit_dates_chk check (end_date >= start_date),
    add constraint permit_description_chk check (char_length(description) between 1 and 200);

create index permit_type_idx on tea.permits (permit_type_id) where (sys_status = 'A');
create index permit_holder_idx on tea.permits (holder_id) where (sys_status = 'A');
create index permit_granted_by_idx on tea.permits (granted_by_id) where (sys_status = 'A');

insert into tea.permit_type (name) values
    ('Resident'),
    ('Visitor'),
    ('Staff'),
    ('Service');
--
