package io.github.gerardpi.easy.jpaentities.processor.entitydefs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class EntityClassDef {
    public static final String CLASSNAME_PERSISTABLE_ENTITY = "PersistableEntity";
    public static final String CLASSNAME_PERSISTABLE_ENTITY_WITH_TAG = "PersistableEntityWithTag";
    public static final String CLASSNAME_ENTITY_DTO = "EntityDto";
    public static final String CLASSNAME_ENTITY_DTO_WITH_TAG = "EntityDtoWithTag";

    private final String name;
    private final List<EntityFieldDef> fieldDefs;
    private final String extendsFromClass;
    private final boolean readOnly; // Can only be used to read from database
    private final String dtoTargetPackage;

    private final List<String> annotations;

    private EntityClassDef(final String name, final List<EntityFieldDef> fieldDefs, final String extendsFromClass, final boolean readOnly, final List<String> annotations, final String dtoTargetPackage) {
        this.name = name;
        this.fieldDefs = fieldDefs;
        this.extendsFromClass = extendsFromClass;
        this.readOnly = readOnly;
        this.annotations = annotations;
        this.dtoTargetPackage = dtoTargetPackage;
    }

    private EntityClassDef(final Builder builder) {
        this(requireNonNull(builder.name, "A name is required"),
                requireNonNull(builder.getFieldDefs(), "A collection of field definitions is required. It may be an empty collection."),
                builder.extendsFromClass,
                builder.readOnly,
                requireNonNull(builder.annotations, "A collection of anntations is required. It may be an empty collection."),
                builder.dtoTargetPackage);
    }

    public static boolean isPersistableEntityWithTag(final String superClassName) {
        return CLASSNAME_PERSISTABLE_ENTITY_WITH_TAG.equals(superClassName);
    }

    public static boolean isPersistableEntityClass(final String superClassName) {
        return CLASSNAME_PERSISTABLE_ENTITY.equals(superClassName);
    }

    public boolean isPersistableEntity() {
        if (extendsFromClass != null) {
            return CLASSNAME_PERSISTABLE_ENTITY.equals(extendsFromClass);
        }
        return false;
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

    public boolean hasTag() {
        return getExtendsFromClass()
                .map(className -> CLASSNAME_ENTITY_DTO_WITH_TAG.equals(className) || CLASSNAME_PERSISTABLE_ENTITY_WITH_TAG.equals(className))
                .orElse(false);
    }


    public Optional<String> getSuperClass(final boolean forDtoClass, final String commonPackage) {
        return getExtendsFromClass()
                .map(superClass -> {
                    if (forDtoClass) {
                        return Optional.of(isPersistableEntityWithTag(superClass)
                                ? commonPackage + "." + CLASSNAME_ENTITY_DTO_WITH_TAG
                                : commonPackage + "." + CLASSNAME_ENTITY_DTO);
                    }
                    return Optional.of(isPersistableEntityWithTag(superClass)
                            ? commonPackage + "." + CLASSNAME_PERSISTABLE_ENTITY_WITH_TAG
                            : commonPackage + "." + CLASSNAME_PERSISTABLE_ENTITY);
                })
                .orElseGet(Optional::empty);
    }

    public boolean isEntity() {
        return isPersistableEntity() || hasTag();
    }

    /**
     * Both DTO as well as Persistable entities are identifiable.
     * Embeddable classes, however, are not.
     */
    public boolean isIdentifiable() {
        return getExtendsFromClass()
                .map(className -> CLASSNAME_PERSISTABLE_ENTITY.equals(className)
                        || CLASSNAME_PERSISTABLE_ENTITY_WITH_TAG.equals(className)
                        || CLASSNAME_ENTITY_DTO.equals(className)
                        || CLASSNAME_ENTITY_DTO_WITH_TAG.equals(className)
                )
                .orElse(false);
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
        private final String dtoTargetPackage;
        private String defaultFieldType;
        private String name;

        @JsonCreator
        public Builder(
                @JsonProperty(value = "fieldDefs", required = true) final List<EntityFieldDef.Builder> fieldDefBuilders,
                @JsonProperty(value = "extendsFromClass") final String extendsFromClass,
                @JsonProperty(value = "annotation") final String annotation,
                @JsonProperty(value = "defaultType") final String defaultFieldType,
                @JsonProperty(value = "annotations") final List<String> annotations,
                @JsonProperty("readOnly") final boolean readOnly,
                @JsonProperty(value = "dtoTargetPackage", defaultValue = "") final String dtoTargetPackage) {
            this.name = null;
            this.fieldDefBuilders = fieldDefBuilders;
            this.extendsFromClass = extendsFromClass;
            this.readOnly = readOnly;
            this.annotations = toImmutableList(annotation, annotations);
            this.defaultFieldType = defaultFieldType;
            this.dtoTargetPackage = dtoTargetPackage;
        }

        private static List<String> toImmutableList(final String annotation, final List<String> annotations) {
            final List<String> allAnnotations = new ArrayList<>();
            if (annotations != null) {
                allAnnotations.addAll(annotations);
            }
            if (annotation != null) {
                allAnnotations.add(annotation);
            }
            return Collections.unmodifiableList(allAnnotations);
        }

        public Builder setName(final String name) {
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

        public Builder setDefaultFieldTypeIfNotSpecified(final String defaultFieldType) {
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
