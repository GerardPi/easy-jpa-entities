package io.github.gerardpi.easy.jpaentities.processor;

import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EasyJpaEntitiesConfig;
import io.github.gerardpi.easy.jpaentities.processor.entitydefs.EntityClassDef;

import java.io.IOException;
import java.io.UncheckedIOException;

class JavaSourceGenerator {
    private final AnnotationProcessorIo io;
    private final AnnotationProcessorLogger log;

    public JavaSourceGenerator(final AnnotationProcessorIo io, final AnnotationProcessorLogger log) {
        this.io = io;
        this.log = log;
    }

    void generateEntityClasses(final EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        easyJpaEntitiesConfig.getEntityClassDefs().forEach(classDef -> {
            final String fqn = easyJpaEntitiesConfig.getTargetPackage() + "." + classDef.getName();
            log.info("Generating entity class " + fqn);
            try (final JavaSourceWriter writer = io.createJavaSourceWriter(fqn)) {
                new EntityClassGenerator(classDef, easyJpaEntitiesConfig)
                        .write(writer);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    void generateDtoClasses(final EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        easyJpaEntitiesConfig.getEntityClassDefs().forEach(classDef -> {
            if (classDef.getDtoTargetPackage().isPresent()) {
                final String fqn = classDef.getDtoTargetPackage().get() + "." + classDef.getName() + "Dto";
                log.info("Generating DTO class " + fqn);
                try (final JavaSourceWriter writer = io.createJavaSourceWriter(fqn)) {
                    new EntityClassGenerator(classDef, easyJpaEntitiesConfig, true)
                            .write(writer);
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
            } else {
                log.info("No DTO class generated for '" + classDef.getName() + "'");
            }
        });
    }

    void generateMappedSuperclasses(final EasyJpaEntitiesConfig easyJpaEntitiesConfig) {
        final SuperclassGenerator superClassGenerator = new SuperclassGenerator(easyJpaEntitiesConfig);

        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_PERSISTABLE_ENTITY, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_PERSISTABLE_ENTITY_WITH_TAG, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_ENTITY_DTO, superClassGenerator);
        writeSuperclass(easyJpaEntitiesConfig, EntityClassDef.CLASSNAME_ENTITY_DTO_WITH_TAG, superClassGenerator);
    }

    private void writeSuperclass(final EasyJpaEntitiesConfig config, final String superClassName, final SuperclassGenerator superclassGenerator) {
        log.info("Generating base class " + superClassName);
        final String fqn = config.getTargetPackage() + "." + superClassName;
        try (final LineWriter writer = io.createLineWriter(fqn)) {
            superclassGenerator.write(superClassName, writer);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
