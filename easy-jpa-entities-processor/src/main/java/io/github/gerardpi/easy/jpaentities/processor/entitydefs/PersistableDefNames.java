package io.github.gerardpi.easy.jpaentities.processor.entitydefs;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;


public class PersistableDefNames {
    private final List<String> entityClassDefNames;

    @JsonCreator
    public PersistableDefNames(@JsonProperty(value = "entityClassDefNames", required = true) List<String> entityClassDefNames) {
        this.entityClassDefNames = entityClassDefNames == null ? Collections.emptyList() : entityClassDefNames;
    }

    public List<String> getEntityClassDefNames() {
        return entityClassDefNames;
    }
}
