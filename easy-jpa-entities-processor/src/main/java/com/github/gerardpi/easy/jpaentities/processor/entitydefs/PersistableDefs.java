package com.github.gerardpi.easy.jpaentities.processor.entitydefs;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class PersistableDefs {
    private final List<EntityClassDef> entityClassDefs;
    private boolean writeRewritablePersistable;
    private boolean writePersistable;

    @JsonCreator
    public PersistableDefs(@JsonProperty("entityClassDefs") List<EntityClassDef> entityClassDefs) {
        this.entityClassDefs = entityClassDefs == null ? Collections.emptyList() : entityClassDefs;

        List<EntityClassDef> rewritableEntityClassDefs = this.entityClassDefs.stream()
                .filter(EntityClassDef::isRewritable)
                .collect(Collectors.toList());
        this.writeRewritablePersistable = !rewritableEntityClassDefs.isEmpty();
        this.writePersistable = rewritableEntityClassDefs.size() != entityClassDefs.size();
    }

    public boolean isWriteRewritablePersistable() {
        return writeRewritablePersistable;
    }

    public boolean isWritePersistable() {
        return writePersistable;
    }

    public List<EntityClassDef> getEntityClassDefs() {
        return entityClassDefs;
    }
}
