package com.github.gerardpi.easy.jpaentities.processor.entitydefs;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;


public class PersistableDefs {
    private final List<EntityClassDef> entityClassDefs;

    @JsonCreator
    public PersistableDefs(
            @JsonProperty("entityClassDefs") List<EntityClassDef> entityClassDefs) {
        this.entityClassDefs = entityClassDefs == null ? Collections.emptyList() : entityClassDefs;
    }


    public List<EntityClassDef> getEntityClassDefs() {
        return entityClassDefs;
    }
}
