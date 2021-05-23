package io.github.gerardpi.easy.jpaentities.processor.entitydefs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.gerardpi.easy.jpaentities.processor.MappedSuperclassGenerator;

import java.util.List;
import java.util.Objects;

public class EntityClassDef {
    private final String name;
    private final List<EntityFieldDef> fieldDefs;
    private final String extendsFromClass;
    private final boolean readOnly; // Can only be used to read from database

    public EntityClassDef(
            String name,
            List<EntityFieldDef> fieldDefs,
            String extendsFromClass,
            boolean readOnly
    ) {
        this.name = name;
        this.fieldDefs = fieldDefs;
        this.extendsFromClass = extendsFromClass;
        this.readOnly = readOnly;
    }
    @JsonCreator
    public EntityClassDef(
            @JsonProperty(value = "fieldDefs", required = true) List<EntityFieldDef> fieldDefs,
            @JsonProperty("extendsFromClass") String extendsFromClass,
            @JsonProperty("readOnly") boolean readOnly
    ) {
        this(null, fieldDefs, extendsFromClass, readOnly);
    }

    private EntityClassDef(Builder builder) {
        this(Objects.requireNonNull(builder.name), Objects.requireNonNull(builder.fieldDefs), Objects.requireNonNull(builder.extendsFromClass), builder.readOnly);
    }

    public String getName() {
        return name;
    }

    public String getExtendsFromClass() {
        return extendsFromClass;
    }

    public List<EntityFieldDef> getFieldDefs() {
        return fieldDefs;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isOptLockable() {
        if (extendsFromClass != null) {
            return MappedSuperclassGenerator.CLASSNAME_OPT_LOCKABLE_PERSISTABLE.equals(extendsFromClass);
        }
        return false;
    }
    public EntityClassDef withName(String name) {
        return new EntityClassDef(name, this.fieldDefs, this.extendsFromClass, this.readOnly);
    }
    public static class Builder {
        private String name;
        private List<EntityFieldDef> fieldDefs;
        private String extendsFromClass;
        private boolean readOnly; // Can only be used to read from database

        public Builder(
                String name,
                List<EntityFieldDef> fieldDefs,
                String extendsFromClass,
                boolean readOnly
        ) {
            this.name = name;
            this.fieldDefs = fieldDefs;
            this.extendsFromClass = extendsFromClass;
            this.readOnly = readOnly;
        }

        @JsonCreator
        public Builder(
                @JsonProperty(value = "fieldDefs", required = true) List<EntityFieldDef> fieldDefs,
                @JsonProperty("extendsFromClass") String extendsFromClass,
                @JsonProperty("readOnly") boolean readOnly
        ) {
            this(null, fieldDefs, extendsFromClass, readOnly);
        }
        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public EntityClassDef build() {
            return new EntityClassDef(this);
        }
    }
}
