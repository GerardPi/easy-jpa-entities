package io.github.gerardpi.easy.jpaentities.processor.entitydefs;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    public EntityFieldDef(String name, String type) {
        this(name, null, type, null, Collections.emptyList(), false);
    }

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

    public Optional<CollectionDef> fetchCollectionDef() {
        return Optional.ofNullable(this.collectionDef);
    }

    public CollectionDef getCollectionDef() {
        return this.collectionDef;
    }

    public boolean isCollection() {
        return this.collectionDef != null;
    }

    public static void main(String[] args) {
        EntityFieldDef entityFieldDef = new EntityFieldDef("name", "singular", "type", "annotation", Arrays.asList("a", "b"), true);
        try {
            System.out.println(new ObjectMapper(new YAMLFactory()).writeValueAsString(entityFieldDef));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public String getCollectedType() {
        return requireNonNull(collectionDef).getCollectedType();
    }
}
