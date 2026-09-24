--liquibase formatted sql

--changeset ite4120:tea-schema
--comment Create the tea schema
/* One schema per component. */
create schema if not exists tea;
--
