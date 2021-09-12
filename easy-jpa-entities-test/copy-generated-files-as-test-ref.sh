#!/bin/bash

SOURCE_DIR=target/generated-sources/annotations/io/github/gerardpi/easy/jpaentities/test1/
TARGET_DIR=./src/test/resources/io/github/gerardpi/easy/jpaentities/test1/

cp ${SOURCE_DIR}domain/addressbook/* ${TARGET_DIR}domain/addressbook
cp ${SOURCE_DIR}domain/webshop/* ${TARGET_DIR}domain/webshop
cp ${SOURCE_DIR}persistence/* ${TARGET_DIR}persistence
