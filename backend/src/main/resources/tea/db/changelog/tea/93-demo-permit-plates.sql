--liquibase formatted sql

--changeset ite4120:tea-93-demo-permit-plates context:demo
--comment License plates for the eight demo permits
/* 92-demo-permits.sql already shipped without a plate. Match the holder and
   the reason, and fill the plate only where it is still empty. A permit
   typed in the portal is left as it is. */
update parking.permits p
   set license_plate = v.license_plate
  from (values
    ('mari.tamm@example.com', 'Lives at Akadeemia tee 15 and needs a resident bay.', '123 ABC'),
    ('jaan.kask@example.com', 'ICT faculty staff, daytime parking by the main building.', '456 DEF'),
    ('liis.rebane@example.com', 'Guest lecture week, visitor bay at the library.', '789 GHI'),
    ('karl.poder@example.com', 'Maintenance contractor working on the heating plant.', '234 JKL'),
    ('eva.saar@example.com', 'Campus services staff who also grants other permits.', '567 MNO'),
    ('mari.tamm@example.com', 'Extra visitor permit for a family weekend on campus.', '890 PQR'),
    ('liis.rebane@example.com', 'Moved into student housing and needs an overnight bay.', '321 STU'),
    ('karl.poder@example.com', 'Resident bay next to the sports hall.', '654 VWX')
) as v (holder_email, description, license_plate)
  join parking.person h on h.email = v.holder_email and h.sys_status = 'A'
 where p.holder_id = h.id
   and p.description = v.description
   and p.sys_status = 'A'
   and p.license_plate is null;
--
