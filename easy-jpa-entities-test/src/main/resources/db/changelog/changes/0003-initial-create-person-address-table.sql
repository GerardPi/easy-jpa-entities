--liquibase formatted sql
--changeset gerardpi:create_person_address_table logicalFilePath:independent runOnChange:false
create table person_address (
    id                  UUID NOT NULL,
    person_id           UUID NOT NULL,
    address_id          UUID NOT NULL,
    types               VARCHAR(256) NOT NULL,
    opt_lock_version    INT NOT NULL,
    CONSTRAINT person_address_PK PRIMARY KEY (id)
);
