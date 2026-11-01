--liquibase formatted sql

--changeset ite4120:tea-92-demo-permits context:demo
--comment Demo parking permits for the people already seeded
/* Holders and granters are the people from 90-demo-data.sql and
   91-demo-data.sql. Permit types come from 04-parking-permit.sql. */
insert into tea.permits (permit_type_id, holder_id, granted_by_id, start_date, end_date, description)
select t.id, h.id, g.id, v.start_date, v.end_date, v.description
from (values
    ('Resident', 'mari.tamm@example.com', 'eva.saar@example.com', date '2026-09-01', date '2027-08-31', 'Lives at Akadeemia tee 15 and needs a resident bay.'),
    ('Staff', 'jaan.kask@example.com', 'eva.saar@example.com', date '2026-01-06', date '2026-12-31', 'ICT faculty staff, daytime parking by the main building.'),
    ('Visitor', 'liis.rebane@example.com', 'jaan.kask@example.com', date '2026-03-02', date '2026-03-06', 'Guest lecture week, visitor bay at the library.'),
    ('Service', 'karl.poder@example.com', 'eva.saar@example.com', date '2026-04-01', date '2026-10-31', 'Maintenance contractor working on the heating plant.'),
    ('Staff', 'eva.saar@example.com', 'jaan.kask@example.com', date '2026-01-06', date '2026-12-31', 'Campus services staff who also grants other permits.'),
    ('Visitor', 'mari.tamm@example.com', 'karl.poder@example.com', date '2026-11-10', date '2026-11-12', 'Extra visitor permit for a family weekend on campus.'),
    ('Resident', 'liis.rebane@example.com', 'eva.saar@example.com', date '2026-08-01', date '2027-07-31', 'Moved into student housing and needs an overnight bay.'),
    ('Resident', 'karl.poder@example.com', 'jaan.kask@example.com', date '2026-02-01', date '2027-01-31', 'Resident bay next to the sports hall.')
) as v (type_name, holder_email, granter_email, start_date, end_date, description)
join tea.permit_type t on t.name = v.type_name and t.sys_status = 'A'
join tea.person h on h.email = v.holder_email and h.sys_status = 'A'
join tea.person g on g.email = v.granter_email and g.sys_status = 'A';
--
