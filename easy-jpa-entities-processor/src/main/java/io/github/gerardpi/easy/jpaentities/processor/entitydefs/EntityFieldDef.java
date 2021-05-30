package io.github.gerardpi.easy.jpaentities.processor.entitydefs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class EntityFieldDef {
    private final String name;
    private final String singular;
    private final String type;
    private final List<String> annotations;
    private final boolean notNull;
    private final CollectionDef collectionDef;
    private final boolean writeOnce;

    public EntityFieldDef(Builder builder) {
        this.name = requireNonNull(builder.name, "A name must be specified.");
        this.singular = builder.singular;
        this.type = requireNonNull(builder.type, "Type must be specified.");
        this.collectionDef = builder.getCollectionDef();
        this.writeOnce = builder.writeOnce;
        this.notNull = builder.notNull;
        this.annotations = requireNonNull(builder.annotations, "A collection (may be empty) must be available.");
    }

    public static void main(String[] args) {
        EntityFieldDef entityFieldDef = new EntityFieldDef.Builder("name", "singular", "type", "annotation", Arrays.asList("a", "b"), true, false)
                .build();
        try {
            System.out.println(new ObjectMapper(new YAMLFactory()).writeValueAsString(entityFieldDef));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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

    public boolean isNotNull() {
        return notNull;
    }

    public CollectionDef getCollectionDef() {
        return collectionDef;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public Optional<CollectionDef> fetchCollectionDef() {
        return Optional.ofNullable(this.collectionDef);
    }

    public boolean isWriteOnce() {
        return writeOnce;
    }

    public static class Builder {
        private final String name;
        private final String singular;
        private final List<String> annotations;
        private final boolean notNull;
        private final boolean writeOnce;
        private String type;

        public Builder(@JsonProperty(value = "name", required = true) String name,
                       @JsonProperty(value = "singular") String singular,
                       @JsonProperty(value = "type") String type,
                       @JsonProperty(value = "annotation") String annotation,
                       @JsonProperty(value = "annotations") List<String> annotations,
                       @JsonProperty(value = "notNull") boolean notNull,
                       @JsonProperty(value = "writeOnce") boolean writeOnce) {
            this.name = name;
            this.singular = singular;
            this.notNull = notNull;
            this.writeOnce = writeOnce;
            this.annotations = toImmutableList(annotation, annotations);
            this.type = type;
        }

        private static List<String> toImmutableList(String annotation, List<String> annotations) {
            List<String> allAnnotations = new ArrayList<>();
            if (annotations != null) {
                allAnnotations.addAll(annotations);
            }
            if (annotation != null) {
                allAnnotations.add(annotation);
            }
            return Collections.unmodifiableList(allAnnotations);
        }

        public Builder setTypeIfNotSpecified(String type) {
            if (this.type == null) {
                this.type = type;
            }
            return this;
        }

        public CollectionDef getCollectionDef() {
            if (CollectionDef.isSupportedCollection(type)) {
                return new CollectionDef(type);
            } else {
                return null;
            }
        }

        public EntityFieldDef build() {
            return new EntityFieldDef(this);
        }
    }
}
