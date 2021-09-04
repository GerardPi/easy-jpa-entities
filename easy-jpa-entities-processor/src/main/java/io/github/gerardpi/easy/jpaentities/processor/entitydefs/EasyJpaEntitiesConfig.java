package io.github.gerardpi.easy.jpaentities.processor.entitydefs;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class EasyJpaEntitiesConfig {
    private final List<String> entityClassDefNames;
    private final String targetPackage;
    private final String commonPackage;
    private final boolean includeConstructorWithParameters;
    /**
     * If jackson-module-paranamer is used, Jackson @JsonProperty annotations are not required.
     * If NOT using jacksom-module-paranamer, youl'll have to specify this property to "true"
     */
    private final boolean dtoWithJsonPropertyAnnotations;
    private final Class<?> idClass;
    private final List<EntityClassDef> entityClassDefs;
    private final boolean hasOptLockablePersistable;
    private final boolean hasPersistable;
    private final String defaultFieldType;
    private final boolean includeCommentWithTimestamp;

    public EasyJpaEntitiesConfig(final Builder builder) {
        this.entityClassDefNames = builder.entityClassDefNames;
        this.targetPackage = builder.targetPackage;
        this.includeConstructorWithParameters = builder.includeConstructorWithParameters;
        this.idClass = builder.idClass;
        this.entityClassDefs = builder.entityClassDefs;
        this.hasOptLockablePersistable = builder.hasOptLockablePersistable;
        this.hasPersistable = builder.hasPersistable;
        this.defaultFieldType = builder.defaultFieldType;
        this.includeCommentWithTimestamp = builder.includeCommentWithTimestamp;
        this.dtoWithJsonPropertyAnnotations = builder.dtoWithJsonPropertyAnnotations;
        this.commonPackage = builder.commonPackage;
    }


    public Map<String, String> getTagReplacementMap() {
        return ImmutableMap.of("##_ID_CLASS_##", idClass.getSimpleName(), "##_ID_CLASS_WITH_PACKAGE_##", idClass.getName());
    }

    public List<String> getEntityClassDefNames() {
        return entityClassDefNames;
    }

    public Class<?> getIdClass() {
        return idClass;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public String getCommonPackage() {
        return commonPackage;
    }

    public boolean isIncludeConstructorWithParameters() {
        return includeConstructorWithParameters;
    }

    public List<EntityClassDef> getEntityClassDefs() {
        return entityClassDefs;
    }

    public boolean hasOptLockablePersistable() {
        return hasOptLockablePersistable;
    }

    public boolean hasPersistable() {
        return hasPersistable;
    }

    public boolean isIncludeCommentWithTimestamp() {
        return includeCommentWithTimestamp;
    }

    public boolean isDtoWithJsonPropertyAnnotations() {
        return dtoWithJsonPropertyAnnotations;
    }

    public String getDefaultType() {
        if (this.defaultFieldType == null) {
            return String.class.getName();
        }
        return defaultFieldType;
    }

    public static class Builder {
        private final boolean includeConstructorWithParameters;
        private final Class<?> idClass;
        private final String defaultFieldType;
        private final List<String> entityClassDefNames;
        private final String commonPackage;
        private final boolean includeCommentWithTimestamp;
        private final boolean dtoWithJsonPropertyAnnotations;
        private String targetPackage;
        private List<EntityClassDef> entityClassDefs;
        private boolean hasOptLockablePersistable;
        private boolean hasPersistable;

        @JsonCreator
        public Builder(
                @JsonProperty(value = "targetPackage") final String targetPackage,
                @JsonProperty(value = "includeConstructorWithParameters", defaultValue = "false") final boolean includeConstructorWithParameters,
                @JsonProperty(value = "entityClassDefNames", required = true) final List<String> entityClassDefNames,
                @JsonProperty(value = "idClass", defaultValue = "java.util.UUID") final String idClassName,
                @JsonProperty(value = "includeCommentWithTimestamp", defaultValue = "true") final boolean includeCommentWithTimestamp,
                @JsonProperty(value = "dtoWithJsonPropertyAnnotation", defaultValue = "false") final boolean dtoWithJsonPropertyAnnotations,
                @JsonProperty(value = "defaultType") final String defaultFieldType,
                @JsonProperty(value = "commonPackage", defaultValue = "") final String commonPackage) {
            this.targetPackage = targetPackage;
            this.includeConstructorWithParameters = includeConstructorWithParameters;
            this.entityClassDefNames = entityClassDefNames == null ? Collections.emptyList() : entityClassDefNames;
            this.idClass = idClassForName(idClassName);
            this.defaultFieldType = defaultFieldType == null ? String.class.getName() : defaultFieldType;
            this.includeCommentWithTimestamp = includeCommentWithTimestamp;
            this.dtoWithJsonPropertyAnnotations = dtoWithJsonPropertyAnnotations;
            this.commonPackage = "".equals(commonPackage) ? targetPackage : commonPackage;
        }

        private static Class<?> idClassForName(final String idClassName) {
            try {
                return Class.forName(idClassName == null ? UUID.class.getName() : idClassName);
            } catch (final ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        public Builder setDefaultIfNoTargetPackageSpecified(final String defaultTargetPackage) {
            if (this.targetPackage == null) {
                this.targetPackage = defaultTargetPackage;
            }
            return this;
        }

        public Builder setEntityClassDefs(final List<EntityClassDef> newEntityClassDefs) {
            final List<EntityClassDef> optLockableClassDefs = newEntityClassDefs.stream()
                    .filter(EntityClassDef::hasTag)
                    .collect(Collectors.toList());

            this.hasOptLockablePersistable = !optLockableClassDefs.isEmpty();
            this.hasPersistable = optLockableClassDefs.size() != newEntityClassDefs.size();
            this.entityClassDefs = newEntityClassDefs;
            return this;
        }

        public EasyJpaEntitiesConfig build() {
            return new EasyJpaEntitiesConfig(this);
        }

        public List<String> getEntityClassDefNames() {
            return entityClassDefNames;
        }

        public String getTargetPackage() {
            return targetPackage;
        }

        public String getDefaultFieldType() {
            return defaultFieldType;
        }
    }
}
