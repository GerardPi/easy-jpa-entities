package io.github.gerardpi.easy.jpaentities.test1;

import com.google.common.collect.ImmutableList;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.Address;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonAddress;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonName;
import io.github.gerardpi.easy.jpaentities.test1.domain.webshop.Currency;
import io.github.gerardpi.easy.jpaentities.test1.domain.webshop.Item;
import io.github.gerardpi.easy.jpaentities.test1.domain.webshop.ItemOrder;
import io.github.gerardpi.easy.jpaentities.test1.domain.webshop.ItemOrderLine;
import io.github.gerardpi.easy.jpaentities.test1.persistence.EntityDto;
import io.github.gerardpi.easy.jpaentities.test1.persistence.EntityDtoWithTag;
import io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntity;
import io.github.gerardpi.easy.jpaentities.test1.persistence.PersistableEntityWithTag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class AssertExpectedSourcesAreGeneratedTest {
    private static final Logger LOG = LoggerFactory.getLogger(AssertExpectedSourcesAreGeneratedTest.class);
    private static final String CURRENT_PATH = new File("").getAbsolutePath();

    private final List<Class<?>> classesToCheck = ImmutableList.<Class<?>>builder()
            .add(Address.class)
            .add(Person.class)
            .add(PersonAddress.class)
            .add(PersonName.class)
            .add(Currency.class)
            .add(Item.class)
            .add(ItemOrder.class)
            .add(ItemOrderLine.class)
            .add(EntityDto.class)
            .add(EntityDtoWithTag.class)
            .add(PersistableEntity.class)
            .add(PersistableEntityWithTag.class)
            .build();

    private static void assertSourcefile(final String className) {
        final String javaSourcePathRelative = className.replace(".", "/") + ".java";
        final Path actualSourcePath = Paths.get(CURRENT_PATH + "/target/generated-sources/annotations/" + javaSourcePathRelative);
        if (!Files.exists(actualSourcePath)) {
            LOG.warn("Could not find '{}'", actualSourcePath);
            fail();
        } else {
            final String actualSources = loadFile(actualSourcePath);
            final String expectedSources = loadFile(AssertExpectedSourcesAreGeneratedTest.class.getResourceAsStream("/" + javaSourcePathRelative));
            assertThat(actualSources).isEqualTo(expectedSources);
            LOG.info("Checked '{}'", actualSourcePath);
        }
    }

    private static String loadFile(final Path path) {
        try {
            return loadFile(Files.newInputStream(path, StandardOpenOption.READ));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String loadFile(final InputStream inputStream) {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void testExpectedJavaSourcesAreGenerated() {
        classesToCheck.stream()
                .map(Class::getName).collect(Collectors.toList())
                .forEach(AssertExpectedSourcesAreGeneratedTest::assertSourcefile);
    }

}
