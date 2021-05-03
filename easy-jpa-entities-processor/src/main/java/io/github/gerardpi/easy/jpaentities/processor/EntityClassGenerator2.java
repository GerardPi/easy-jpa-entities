package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityFieldDef;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityClassGenerator2 {
    private final EntityClassDef classDef;
    private final String targetPackage;
    private final boolean includeConstructorWithParameters;
    private final Class<?> idClass;
    private final List<EntityClassDef> entityClassDefs;

    public EntityClassGenerator2(EntityClassDef classDef, EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        this.classDef = classDef;
        this.targetPackage = easyJpaEntitiesConfig.getTargetPackage();
        this.includeConstructorWithParameters = easyJpaEntitiesConfig.isIncludeConstructorWithParameters();
        this.entityClassDefs = easyJpaEntitiesConfig.getEntityClassDefs();
        this.idClass = easyJpaEntitiesConfig.getIdClass();
    }

    public void write(JavaSourceWriter writer) {
        writer.writePackageLine(targetPackage)
                .emptyLine()
                .writeLine("// Generated date/time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .writeLine("@javax.persistence.Entity")
                .writeLine("@javax.persistence.Access(javax.persistence.AccessType.FIELD)");
        if (classDef.isReadOnly()) {
            writer.writeLine("@org.hibernate.annotations.Immutable");
        }
        writer
                .writeClassDeclaration(classDef)
                .writeEntityFieldDeclarations(classDef.getFieldDefs())
                .writeConstructors(classDef, includeConstructorWithParameters)
                .writeFieldGetters(classDef.getFieldDefs())
                .writeToStringMethod(classDef.isOptLockable(), classDef.getFieldDefs());

        if (!classDef.isReadOnly()) {
            writeBuilderParts(writer);
        }

        writer.writeBlockEnd();
    }
    private void writeBuilderParts(JavaSourceWriter writer) {
        List<String> otherEntityClassNames = entityClassDefs.stream().map(EntityClassDef::getName).collect(Collectors.toList());
        writer
                .writeCreateAndModifyWithBuilderMethods(idClass)
                .writeBlockBeginln("public static class Builder")
                .writeBuilderFieldDeclarations(classDef.getFieldDefs(), otherEntityClassNames)
                .writeFieldDeclarationWithoutPropName(new EntityFieldDef("existing", classDef.getName()), true, otherEntityClassNames, true)
                .writeFieldDeclarationWithoutPropName(new EntityFieldDef("id", idClass.getName()), true, otherEntityClassNames, true);
        if (!classDef.isOptLockable()) {
            writer.writeFieldDeclarationWithoutPropName(new EntityFieldDef("isNew", "boolean"), true, otherEntityClassNames, true);
        }
        if (classDef.isOptLockable()) {
            writer.writeFieldDeclarationWithoutPropName(new EntityFieldDef("optLockVersion" , Integer.class.getName()), true, otherEntityClassNames, true);
        }
        writer
                .emptyLine()
                .writeBlockBeginln("private Builder(" + idClass.getName() + " id)")
                .writeLine("this.id = java.util.Objects.requireNonNull(id);")
                .writeLine("this.existing = null;");
        if (!classDef.isOptLockable()) {
            writer.writeLine("this.isNew = true;");
        }

        if (classDef.isOptLockable()) {
            writer.writeLine("this.optLockVersion = null;");
        }
        writer
                .writeAssignmentsToNull(classDef.getFieldDefs())
                .writeBlockEnd()
                .emptyLine()
                .writeBlockBeginln("private Builder(" + classDef.getName() + " existing)")
                .writeLine("this.existing = java.util.Objects.requireNonNull(existing);")
                .writeLine("this.id = existing.getId();");
        if (!classDef.isOptLockable()) {
            writer.writeLine("this.isNew = false;");
        }

        if (classDef.isOptLockable()) {
            writer
                    .writeLine("this.optLockVersion = existing.getOptLockVersion();");
        }
        writer.writeAssignmentsInBuilderConstructor(classDef.getFieldDefs(), "this.", "existing.", otherEntityClassNames);
        writer.writeBlockEnd();
        writer.emptyLine();
        writer.writeBuilderSetters(classDef);
        writer.writeBuilderBuildMethod(classDef);
        writer.writeBlockEnd();
    }
}
