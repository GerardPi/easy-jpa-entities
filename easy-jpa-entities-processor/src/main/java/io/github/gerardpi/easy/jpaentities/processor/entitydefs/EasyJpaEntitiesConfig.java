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

    @JsonCreator
    public EasyJpaEntitiesConfig(
            @JsonProperty(value = "targetPackage") String targetPackage,
            @JsonProperty(value = "includeConstructorWithParameters", defaultValue = "false") boolean includeConstructorWithParameters,
            @JsonProperty(value = "entityClassDefNames", required = true) List<String> entityClassDefNames,
            @JsonProperty(value = "idClass", defaultValue = "java.util.UUID") String idClassName) {
        this(targetPackage, includeConstructorWithParameters, entityClassDefNames, idClassForName(idClassName));
    }

    public EasyJpaEntitiesConfig(List<String> entityClassDefNames) {
        this(null, false, entityClassDefNames, UUID.class);
    }

    private EasyJpaEntitiesConfig(
            String targetPackage,
            boolean includeConstructorWithParameters,
            List<String> entityClassDefNames,
            Class<?> idClass) {
        this(targetPackage, includeConstructorWithParameters, entityClassDefNames, idClass, Collections.emptyList(), false, false);
    }

    private EasyJpaEntitiesConfig(
            String targetPackage,
            boolean includeConstructorWithParameters,
            List<String> entityClassDefNames,
            Class<?> idClass,
            List<EntityClassDef> entityClassDefs, boolean hasOptLockablePersistable, boolean hasPersistable) {
        this.entityClassDefNames = entityClassDefNames == null ? Collections.emptyList() : entityClassDefNames;
        this.targetPackage = targetPackage;
        this.includeConstructorWithParameters = includeConstructorWithParameters;
        this.idClass = idClass;
        this.entityClassDefs = entityClassDefs;
        this.hasOptLockablePersistable = hasOptLockablePersistable;
        this.hasPersistable = hasPersistable;
    }

    private static Class<?> idClassForName(String idClassName) {
        try {
            return Class.forName(idClassName == null ? UUID.class.getName() : idClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
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

    public EasyJpaEntitiesConfig withDefaultTargetPackageIfNotSpecified(String defaultTargetPackage) {
        if (this.targetPackage == null) {
            return new EasyJpaEntitiesConfig(defaultTargetPackage, this.includeConstructorWithParameters, this.entityClassDefNames, this.idClass);
        }
        return this;
    }


    public EasyJpaEntitiesConfig withEntityClassDefs(List<EntityClassDef> newEntityClassDefs) {
        List<EntityClassDef> optLockableClassDefs = newEntityClassDefs.stream()
                .filter(EntityClassDef::isOptLockable)
                .collect(Collectors.toList());

        boolean hasOptLockablePersistable = !optLockableClassDefs.isEmpty();
        boolean hasPersistable = optLockableClassDefs.size() != newEntityClassDefs.size();
        return new EasyJpaEntitiesConfig(
                this.targetPackage, this.includeConstructorWithParameters,
                this.entityClassDefNames, this.idClass,
                newEntityClassDefs, hasOptLockablePersistable, hasPersistable);
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
}
