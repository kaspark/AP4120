--liquibase formatted sql

--changeset ite4120:tea-90-demo-data context:demo
--comment Demo user and the Earl Grey example
/* Applied only when the Liquibase context includes demo. */
insert into tea.tea_user (name, email) values
    ('Mari Tamm', 'mari.tamm@example.com');

insert into tea.tea (name, brand, category_id, owner_id, purchase_date, expiry_date, quantity, unit)
select 'Earl Grey', 'Twinings', c.id, u.id, date '2026-09-01', date '2027-09-01', 45, 'bags'
from tea.category c
join tea.tea_user u on u.email = 'mari.tamm@example.com'
where c.name = 'Black';
--
