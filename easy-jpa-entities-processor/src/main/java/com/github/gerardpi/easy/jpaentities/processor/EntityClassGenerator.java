package com.github.gerardpi.easy.jpaentities.processor;

import com.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

public class EntityClassGenerator {
    private final EntityClassDef classDef;
    private final String targetPackage;
    private final boolean includeConstructorWithParameters;
    private final Class<?> idClass;

    public EntityClassGenerator(EntityClassDef classDef, String targetPackage, boolean includeConstructorWithParameters, Class<?> idClass) {
        this.classDef = classDef;
        this.targetPackage = targetPackage;
        this.includeConstructorWithParameters = includeConstructorWithParameters;
        this.idClass = idClass;
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
                .writeToStringMethod(classDef.isRewritable(), classDef.getFieldDefs());

        if (!classDef.isReadOnly()) {
            writeBuilderParts(writer);
        }

        writer.writeBlockEnd();
    }
    private void writeBuilderParts(JavaSourceWriter writer) {
        writer
                .writeCreateAndModifyWithBuilderMethods(idClass)
                .writeBlockBeginln("public static class Builder")
                .writeBuilderFieldDeclarations(classDef.getFieldDefs())
                .writeFieldDeclaration(classDef.getName(), "existing", true, Collections.emptyList())
                .writeFieldDeclaration(idClass.getName(), "id", true, Collections.emptyList());
        if (!classDef.isRewritable()) {
            writer.writeFieldDeclaration("boolean", "isNew", true, Collections.emptyList());
        }
        if (classDef.isRewritable()) {
            writer.writeFieldDeclaration(Integer.class.getName(), "optLockVersion", true, Collections.emptyList());
        }
        writer
                .emptyLine()
                .writeBlockBeginln("private Builder(" + idClass.getName() + " id)")
                .writeLine("this.id = java.util.Objects.requireNonNull(id);")
                .writeLine("this.existing = null;");
        if (!classDef.isRewritable()) {
            writer.writeLine("this.isNew = true;");
        }

        if (classDef.isRewritable()) {
            writer.writeLine("this.optLockVersion = null;");
        }
        writer
                .writeAssignmentsToNull(classDef.getFieldDefs())
                .writeBlockEnd()
                .emptyLine()
                .writeBlockBeginln("private Builder(" + classDef.getName() + " existing)")
                .writeLine("this.existing = java.util.Objects.requireNonNull(existing);")
                .writeLine("this.id = existing.getId();");
        if (!classDef.isRewritable()) {
            writer.writeLine("this.isNew = false;");
        }

        if (classDef.isRewritable()) {
            writer
                    .writeLine("this.optLockVersion = existing.getOptLockVersion();");
        }
        writer.writeAssignmentsInBuilderConstructor(classDef.getFieldDefs(), "this.", "existing.");
        writer.writeBlockEnd();
        writer.emptyLine();
        writer.writeBuilderSetters(classDef);
        writer.writeBuilderBuildMethod(classDef);
        writer.writeBlockEnd();
    }

}
