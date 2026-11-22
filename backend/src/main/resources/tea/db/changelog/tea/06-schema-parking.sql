--liquibase formatted sql

--changeset ite4120:tea-06-schema-parking
--comment Rename schema tea to parking
/* The shipped changesets created schema tea. The data model is schema parking.
   Renaming moves the tables, indexes, and triggers with it. */
alter schema tea rename to parking;
--
