package com.github.gerardpi.easy.jpaentities.processor.entitydefs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.gerardpi.easy.jpaentities.processor.MappedSuperclassGenerator;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityClassDef {
    private final String name;
    private final List<EntityFieldDef> fieldDefs;
    private final String extendsFromClass;
    private final boolean readOnly; // Can only be used to read from database

    @JsonCreator
    public EntityClassDef(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "fieldDefs", required = true) List<EntityFieldDef> fieldDefs,
            @JsonProperty("extendsFromClass") String extendsFromClass,
            @JsonProperty("readOnly") boolean readOnly
    ) {
        this.name = name;
        this.fieldDefs = fieldDefs;
        this.extendsFromClass = extendsFromClass;
        this.readOnly = readOnly;
    }

    public String getName() {
        return name;
    }

    public String getExtendsFromClass() {
        return extendsFromClass;
    }

    public List<EntityFieldDef> getFieldDefs() {
        return fieldDefs;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isRewritable() {
        if (extendsFromClass != null) {
            return MappedSuperclassGenerator.CLASSNAME_REWRITABLE_PERSISTABLE.equals(extendsFromClass);
        }
        return false;
    }
}
