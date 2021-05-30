package io.github.gerardpi.easy.jpaentities.processor.entitydefs;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.*;

public class EntityFieldDef {
    private final String name;
    private final String singular;
    private final String type;
    private final List<String> annotations;
    private final boolean notNull;
    private final CollectionDef collectionDef;
    private final boolean writeOnce;

    public EntityFieldDef(String name, String type) {
        this(name, null, type, null, Collections.emptyList(), false, false);
    }

    @JsonCreator
    public EntityFieldDef(@JsonProperty(value = "name", required = true) String name,
                          @JsonProperty(value = "singular") String singular,
                          @JacksonInject
                          @JsonProperty(value = "type", required = true) String type,
                          @JsonProperty(value = "annotation") String annotation,
                          @JsonProperty(value = "annotations") List<String> annotations,
                          @JsonProperty(value = "notNull") boolean notNull,
                          @JsonProperty(value = "writeOnce") boolean writeOnce) {
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
        this.writeOnce = writeOnce;
    }

    public static void main(String[] args) {
        EntityFieldDef entityFieldDef = new EntityFieldDef("name", "singular", "type", "annotation", Arrays.asList("a", "b"), true, false);
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

    public List<String> getAnnotations() {
        return annotations;
    }

    public Optional<CollectionDef> fetchCollectionDef() {
        return Optional.ofNullable(this.collectionDef);
    }

    public boolean isWriteOnce() {
        return writeOnce;
    }

    public static class InjectableValues extends com.fasterxml.jackson.databind.InjectableValues.Std {
        private final String fieldName;

        public InjectableValues(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
            System.out.println("######## forProperty.name='" + forProperty.getName() + "'");
            return super.findInjectableValue(valueId, ctxt, forProperty, beanInstance);
        }
    }
}
