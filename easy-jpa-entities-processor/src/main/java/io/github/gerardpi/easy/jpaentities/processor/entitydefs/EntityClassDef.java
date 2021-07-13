package io.github.gerardpi.easy.jpaentities.processor.entitydefs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.github.gerardpi.easy.jpaentities.processor.MappedSuperclassGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class EntityClassDef {
    private final String name;
    private final List<EntityFieldDef> fieldDefs;
    private final String extendsFromClass;
    private final boolean readOnly; // Can only be used to read from database
    private final String dtoTargetPackage;
    private final List<String> annotations;

    private EntityClassDef(String name, List<EntityFieldDef> fieldDefs, String extendsFromClass, boolean readOnly, List<String> annotations, String dtoTargetPackage) {
        this.name = name;
        this.fieldDefs = fieldDefs;
        this.extendsFromClass = extendsFromClass;
        this.readOnly = readOnly;
        this.annotations = annotations;
        this.dtoTargetPackage = dtoTargetPackage;
    }

    private EntityClassDef(Builder builder) {
        this(requireNonNull(builder.name, "A name is required"),
                requireNonNull(builder.getFieldDefs(), "A collection of field definitions is required. It may be an empty collection."),
                builder.extendsFromClass,
                builder.readOnly,
                requireNonNull(builder.annotations, "A collection of anntations is required. It may be an empty collection."),
                builder.dtoTargetPackage);
    }

    public String getName() {
        return name;
    }

    public Optional<String> getExtendsFromClass() {
        return Optional.ofNullable(extendsFromClass);
    }

    public List<EntityFieldDef> getFieldDefs() {
        return fieldDefs;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public boolean isOptLockable() {
        if (extendsFromClass != null) {
            return MappedSuperclassGenerator.CLASSNAME_OPT_LOCKABLE_PERSISTABLE.equals(extendsFromClass);
        }
        return false;
    }

    public boolean isPersistable() {
        if (extendsFromClass != null) {
            return MappedSuperclassGenerator.CLASSNAME_PERSISTABLE.equals(extendsFromClass);
        }
        return false;
    }

    public boolean isEntity() {
        return isPersistable() || isOptLockable();
    }

    public Optional<String> getDtoTargetPackage() {
        return Optional.ofNullable(dtoTargetPackage);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("fieldDefs", fieldDefs)
                .add("extendsFromClass", extendsFromClass)
                .add("readOnly", readOnly)
                .add("annotations", annotations)
                .toString();
    }

    public static class Builder {
        private final List<EntityFieldDef.Builder> fieldDefBuilders;
        private final String extendsFromClass;
        private final boolean readOnly; // Can only be used to read from database
        private final List<String> annotations;
        private String defaultFieldType;
        private String name;
        private String dtoTargetPackage;

        public Builder(
                String name,
                List<EntityFieldDef.Builder> fieldDefBuilders,
                String extendsFromClass,
                boolean readOnly, String annotation,
                List<String> annotations,
                String defaultFieldType,
                String dtoTargetPackage
        ) {
            this.name = name;
            this.fieldDefBuilders = fieldDefBuilders;
            this.extendsFromClass = extendsFromClass;
            this.readOnly = readOnly;
            this.annotations = toImmutableList(annotation, annotations);
            this.defaultFieldType = defaultFieldType;
            this.dtoTargetPackage = dtoTargetPackage;
        }

        @JsonCreator
        public Builder(
                @JsonProperty(value = "fieldDefs", required = true) List<EntityFieldDef.Builder> fieldDefBuilders,
                @JsonProperty(value = "extendsFromClass") String extendsFromClass,
                @JsonProperty(value = "annotation") String annotation,
                @JsonProperty(value = "defaultType") String defaultFieldType,
                @JsonProperty(value = "annotations") List<String> annotations,
                @JsonProperty("readOnly") boolean readOnly,
                @JsonProperty(value = "dtoTargetPackage", defaultValue = "") String dtoTargetPackage) {
            this(null, fieldDefBuilders, extendsFromClass, readOnly, annotation, annotations, defaultFieldType, dtoTargetPackage);
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

        public Builder setName(String name) {
            this.name = name;
            return this;
        }


        public EntityClassDef build() {
            return new EntityClassDef(this);
        }

        public List<EntityFieldDef> getFieldDefs() {
            return fieldDefBuilders.stream()
                    .map(fieldDefBuilder -> fieldDefBuilder
                            .setTypeIfNotSpecified(requireNonNull(this.defaultFieldType, "A default field type is required"))
                            .build())
                    .collect(Collectors.toList());
        }

        public Builder setDefaultFieldTypeIfNotSpecified(String defaultFieldType) {
            if (this.defaultFieldType == null) {
                this.defaultFieldType = defaultFieldType;
            }
            return this;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("fieldDefs", getFieldDefs())
                    .add("extendsFromClass", extendsFromClass)
                    .add("readOnly", readOnly)
                    .add("annotations", annotations)
                    .add("name", name)
                    .toString();
        }
    }
}
