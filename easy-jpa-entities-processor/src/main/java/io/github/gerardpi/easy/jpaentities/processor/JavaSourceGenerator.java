package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.IOException;
import java.io.UncheckedIOException;

import static io.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.createJavaSourceWriter;
import static io.github.gerardpi.easy.jpaentities.processor.ProcessorUtils.note;

class JavaSourceGenerator {
    private final ProcessingEnvironment processingEnv;

    public JavaSourceGenerator(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    void generateEntityClasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        easyJpaEntitiesConfig.getEntityClassDefs().forEach(classDef -> {
            String fqn = easyJpaEntitiesConfig.getTargetPackage() + "." + classDef.getName();
            note(processingEnv, "Generating entity class " + fqn);
            try (JavaSourceWriter writer = createJavaSourceWriter(processingEnv, fqn)) {
                new EntityClassGenerator(classDef, easyJpaEntitiesConfig)
                        .write(writer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    void generateDtoClasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        easyJpaEntitiesConfig.getEntityClassDefs().forEach(classDef -> {
            if (classDef.getDtoTargetPackage().isPresent()) {
                String fqn = classDef.getDtoTargetPackage().get() + "." + classDef.getName() + "Dto";
                note(processingEnv, "Generating DTO class " + fqn);
                try (JavaSourceWriter writer = createJavaSourceWriter(processingEnv, fqn)) {
                    new EntityClassGenerator(classDef, easyJpaEntitiesConfig, true)
                            .write(writer);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            } else {
                note(processingEnv, "No DTO class generated for '" + classDef.getName() + "'");
            }
        });
    }

    void generateMappedSuperclasses(EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        SuperclassGenerator superClassGenerator = new SuperclassGenerator(easyJpaEntitiesConfig);

        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_PERSISTABLE_ENTITY, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_PERSISTABLE_ENTITY_WITH_TAG, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_ENTITY_DTO, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_ENTITY_DTO_WITH_TAG, superClassGenerator);
    }

    private void writeSuperclass(EasyJpaEntitiesConfig config, String superClassName, SuperclassGenerator superclassGenerator) {
        note(processingEnv, "Generating base class " + superClassName);
        String fqn = config.getTargetPackage() + "." + superClassName;
        try (LineWriter writer = ProcessorUtils.createLineWriter(processingEnv, fqn)) {
            superclassGenerator.write(superClassName, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
