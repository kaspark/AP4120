--liquibase formatted sql

--changeset ite4120:tea-05-permit-license-plate
--comment Add the license plate a permit is issued for
/* The plate is text, never a number. Demo permits already exist without one,
   so the column is nullable. A new or edited permit must still send a plate. */
alter table tea.permits
    add column license_plate text;

alter table tea.permits
    add constraint permit_license_plate_chk
        check (license_plate is null or char_length(license_plate) between 1 and 20);
--
