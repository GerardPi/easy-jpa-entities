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
    private final boolean includeConstructorWithParameters;
    private final Class<?> idClass;
    private final List<EntityClassDef> entityClassDefs;
    private final boolean hasOptLockablePersistable;
    private final boolean hasPersistable;
    private final String defaultFieldType;
    private final boolean includeCommentWithTimestamp;

    public EasyJpaEntitiesConfig(Builder builder) {
        this.entityClassDefNames = builder.entityClassDefNames;
        this.targetPackage = builder.targetPackage;
        this.includeConstructorWithParameters = builder.includeConstructorWithParameters;
        this.idClass = builder.idClass;
        this.entityClassDefs = builder.entityClassDefs;
        this.hasOptLockablePersistable = builder.hasOptLockablePersistable;
        this.hasPersistable = builder.hasPersistable;
        this.defaultFieldType = builder.defaultFieldType;
        this.includeCommentWithTimestamp = builder.includeCommentWithTimestamp;
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
        private List<String> entityClassDefNames;
        private String targetPackage;
        private List<EntityClassDef> entityClassDefs;
        private boolean hasOptLockablePersistable;
        private boolean includeCommentWithTimestamp;
        private boolean hasPersistable;

        @JsonCreator
        public Builder(
                @JsonProperty(value = "targetPackage") String targetPackage,
                @JsonProperty(value = "includeConstructorWithParameters", defaultValue = "false") boolean includeConstructorWithParameters,
                @JsonProperty(value = "entityClassDefNames", required = true) List<String> entityClassDefNames,
                @JsonProperty(value = "idClass", defaultValue = "java.util.UUID") String idClassName,
                @JsonProperty(value = "includeCommentWithTimestamp", defaultValue = "true") boolean includeCommentWithTimestamp,
                @JsonProperty(value = "defaultType") String defaultFieldType) {
            this.targetPackage = targetPackage;
            this.includeConstructorWithParameters = includeConstructorWithParameters;
            this.entityClassDefNames = entityClassDefNames == null ? Collections.emptyList() : entityClassDefNames;
            this.idClass = idClassForName(idClassName);
            this.defaultFieldType = defaultFieldType == null ? String.class.getName() : defaultFieldType;
            this.includeCommentWithTimestamp = includeCommentWithTimestamp;
        }

        private static Class<?> idClassForName(String idClassName) {
            try {
                return Class.forName(idClassName == null ? UUID.class.getName() : idClassName);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        public Builder setDefaultIfNoTargetPackageSpecified(String defaultTargetPackage) {
            if (this.targetPackage == null) {
                this.targetPackage = defaultTargetPackage;
            }
            return this;
        }

        public Builder setEntityClassDefs(List<EntityClassDef> newEntityClassDefs) {
            List<EntityClassDef> optLockableClassDefs = newEntityClassDefs.stream()
                    .filter(EntityClassDef::isOptLockable)
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
