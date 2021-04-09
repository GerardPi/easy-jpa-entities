--liquibase formatted sql
--changeset gerardpi:create_person_table logicalFilePath:independent runOnChange:false
create table person (
    id                  UUID NOT NULL,
    name_first          VARCHAR(50) NOT NULL,
    name_last           VARCHAR(50) NOT NULL,
    date_of_birth       DATE(50),
    opt_lock_version    INT NOT NULL,
    CONSTRAINT person_PK PRIMARY KEY (id)
);