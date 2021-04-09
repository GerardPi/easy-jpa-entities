--liquibase formatted sql
--changeset gerardpi:create_address_table logicalFilePath:independent runOnChange:false
create table address (
    id                  UUID NOT NULL,
    country_code        VARCHAR(3) NOT NULL,
    city                VARCHAR(50) NOT NULL,
    postal_code         VARCHAR(50) NOT NULL,
    street              VARCHAR(50),
    house_number        VARCHAR(50) NOT NULL,
    opt_lock_version    INT NOT NULL,
    CONSTRAINT address_PK PRIMARY KEY (id)
);
