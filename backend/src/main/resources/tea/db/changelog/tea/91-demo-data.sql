--liquibase formatted sql

--changeset ite4120:tea-91-demo-data context:demo
--comment Four more owners and twenty-five teas
/* Applied only when the Liquibase context includes demo. Rows match TEA.01 example data. */
insert into tea.tea_user (name, email) values
    ('Jaan Kask', 'jaan.kask@example.com'),
    ('Liis Rebane', 'liis.rebane@example.com'),
    ('Karl Põder', 'karl.poder@example.com'),
    ('Eva Saar', 'eva.saar@example.com');

insert into tea.tea (name, brand, category_id, owner_id, purchase_date, expiry_date, quantity, unit)
select v.name, v.brand, c.id, u.id, v.purchase_date, v.expiry_date, v.quantity, v.unit
from (values
    ('Sencha', 'Ippodo', 'Green', 'mari.tamm@example.com', date '2026-03-12', date '2027-03-12', 80, 'g'),
    ('Dragon Well', 'TeaVivre', 'Green', 'jaan.kask@example.com', date '2026-04-02', date '2027-04-02', 50, 'g'),
    ('Genmaicha', 'Yamamotoyama', 'Green', 'liis.rebane@example.com', date '2026-01-20', date '2027-01-20', 40, 'bags'),
    ('Jasmine Pearl', 'Harney & Sons', 'Green', 'karl.poder@example.com', date '2026-06-15', date '2027-06-15', 30, 'g'),
    ('Gyokuro', 'Marukyu', 'Green', 'eva.saar@example.com', date '2026-05-01', date '2027-05-01', 25, 'g'),
    ('English Breakfast', 'Twinings', 'Black', 'mari.tamm@example.com', date '2026-02-10', date '2028-02-10', 100, 'bags'),
    ('Assam', 'Ahmad Tea', 'Black', 'jaan.kask@example.com', date '2025-11-03', date '2027-11-03', 60, 'bags'),
    ('Darjeeling', 'Makaibari', 'Black', 'liis.rebane@example.com', date '2026-07-08', date '2027-07-08', 40, 'g'),
    ('Lapsang Souchong', 'Taylors', 'Black', 'karl.poder@example.com', date '2026-08-19', date '2028-08-19', 20, 'bags'),
    ('Ceylon', 'Dilmah', 'Black', 'eva.saar@example.com', date '2026-09-14', date '2028-09-14', 75, 'bags'),
    ('Russian Caravan', 'Kusmi', 'Black', 'mari.tamm@example.com', date '2026-01-05', date '2027-06-05', 35, 'bags'),
    ('Keemun', 'Teapigs', 'Black', 'jaan.kask@example.com', date '2026-03-28', date '2027-09-28', 15, 'tins'),
    ('Chamomile', 'Traditional Medicinals', 'Herbal', 'liis.rebane@example.com', date '2026-04-18', date '2028-04-18', 20, 'bags'),
    ('Peppermint', 'Pukka', 'Herbal', 'karl.poder@example.com', date '2026-02-22', date '2028-02-22', 24, 'bags'),
    ('Rooibos', 'Tick Tock', 'Herbal', 'eva.saar@example.com', date '2026-06-01', date '2028-06-01', 50, 'bags'),
    ('Hibiscus', 'Celestial Seasonings', 'Herbal', 'mari.tamm@example.com', date '2026-05-11', date '2027-11-11', 18, 'bags'),
    ('Lemon Verbena', 'Palais des Thés', 'Herbal', 'jaan.kask@example.com', date '2026-07-30', date '2027-12-30', 40, 'g'),
    ('Ginger Turmeric', 'Yogi', 'Herbal', 'liis.rebane@example.com', date '2026-08-04', date '2028-08-04', 16, 'bags'),
    ('Tieguanyin', 'TeaVivre', 'Oolong', 'karl.poder@example.com', date '2026-03-03', date '2027-03-03', 45, 'g'),
    ('Da Hong Pao', 'Wuyi Star', 'Oolong', 'eva.saar@example.com', date '2026-09-20', date '2028-03-20', 30, 'g'),
    ('Milk Oolong', 'Lupicia', 'Oolong', 'mari.tamm@example.com', date '2026-04-25', date '2027-04-25', 55, 'g'),
    ('Dong Ding', 'Mountain Tea', 'Oolong', 'jaan.kask@example.com', date '2026-01-16', date '2027-01-16', 35, 'g'),
    ('Oriental Beauty', 'Tea from Taiwan', 'Oolong', 'liis.rebane@example.com', date '2026-06-21', date '2027-12-21', 20, 'g'),
    ('Osmanthus Oolong', 'Ten Ren', 'Oolong', 'karl.poder@example.com', date '2026-02-14', date '2027-08-14', 28, 'bags'),
    ('Iron Buddha', 'Fujian Tea', 'Oolong', 'eva.saar@example.com', date '2026-10-01', date '2028-04-01', 42, 'g')
) as v (name, brand, category_name, owner_email, purchase_date, expiry_date, quantity, unit)
join tea.category c on c.name = v.category_name and c.sys_status = 'A'
join tea.tea_user u on u.email = v.owner_email and u.sys_status = 'A';
--
