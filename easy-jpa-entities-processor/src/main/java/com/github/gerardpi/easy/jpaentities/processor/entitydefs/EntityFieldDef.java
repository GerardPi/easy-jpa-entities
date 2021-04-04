package com.github.gerardpi.easy.jpaentities.processor.entitydefs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EntityFieldDef {
    private final String name;
    private final String singular;
    private final String type;
    private final List<String> annotations;
    private final boolean notNull;
    private final CollectionDef collectionDef;

    @JsonCreator
    public EntityFieldDef(@JsonProperty(value = "name", required = true) String name,
                          @JsonProperty(value = "singular") String singular,
                          @JsonProperty(value = "type", required = true) String type,
                          @JsonProperty(value = "annotation") String annotation,
                          @JsonProperty(value = "annotations") List<String> annotations,
                          @JsonProperty(value = "notNull") boolean notNull) {
        this.name = name;
        this.singular = singular;
        this.type = type;
        List<String> allAnnotations = new ArrayList<>();
        if (annotations != null) {
            allAnnotations.addAll(annotations);
        }
        if (annotation != null) {
            allAnnotations.add(annotation);
        }
        this.annotations = Collections.unmodifiableList(allAnnotations);
        this.notNull = notNull;
        if (CollectionDef.isSupportedCollection(type)) {
            this.collectionDef = new CollectionDef(type);
        } else {
            this.collectionDef = null;
        }
    }

    public String getName() {
        return name;
    }

    public String getSingular() {
        if (singular == null) {
            return name;
        }
        return singular;
    }

    public String getType() {
        return type;
    }


    public String getAnnotation() {
        if (annotations.isEmpty()) {
            return null;
        }
        return annotations.get(0);
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public Optional<CollectionDef> getCollectionDef() {
        return Optional.ofNullable(this.collectionDef);
    }
}
