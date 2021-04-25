package com.github.gerardpi.easy.jpaentities.processor.entitydefs;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class PersistableDefs {
    private final Class<?> idClass;
    private final List<EntityClassDef> entityClassDefs;
    private boolean writeOptLockablePersistable;
    private boolean writePersistable;

    @JsonCreator
    public PersistableDefs(
        @JsonProperty(value = "idClass") String idClass,
        @JsonProperty(value = "entityClassDefs", required = true) List<EntityClassDef> entityClassDefs) {
        this.entityClassDefs = entityClassDefs == null ? Collections.emptyList() : entityClassDefs;

        List<EntityClassDef> optLockableClassDefs = this.entityClassDefs.stream()
                .filter(EntityClassDef::isOptLockable)
                .collect(Collectors.toList());
        this.writeOptLockablePersistable = !optLockableClassDefs.isEmpty();
        this.writePersistable = optLockableClassDefs.size() != entityClassDefs.size();

        String classNameForId = idClass == null ? UUID.class.getName() : idClass;
        try {
            this.idClass = Class.forName(classNameForId);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public Class<?> getIdClass() {
        return idClass;
    }

    public Map<String, String> getTagReplacementMap() {
        return ImmutableMap.of("##_ID_CLASS_##", idClass.getSimpleName(), "##_ID_CLASS_WITH_PACKAGE_##", idClass.getName());
    }

    public boolean isWriteOptLockablePersistable() {
        return writeOptLockablePersistable;
    }

    public boolean isWritePersistable() {
        return writePersistable;
    }

    public List<EntityClassDef> getEntityClassDefs() {
        return entityClassDefs;
    }
}
