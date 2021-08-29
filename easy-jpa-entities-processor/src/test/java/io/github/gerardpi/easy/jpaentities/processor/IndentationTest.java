package io.github.gerardpi.easy.jpaentities.processor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IndentationTest {
    @Test
    void testIndentations() {
        Indentation indentation = new Indentation(5, 2);
        assertThat(indentation.get()).isEmpty();
        indentation.inc();
        assertThat(indentation.get()).isEqualTo("  ");
        indentation.inc();
        assertThat(indentation.get()).isEqualTo("    ");
        indentation.dec();
        indentation.dec();
        assertThat(indentation.get()).isEmpty();
        indentation.dec();
        assertThat(indentation.get()).isEmpty();
    }
}
