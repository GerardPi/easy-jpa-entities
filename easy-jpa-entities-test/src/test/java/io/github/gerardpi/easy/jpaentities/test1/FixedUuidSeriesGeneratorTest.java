package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.gerardpi.easy.jpaentities.test1.TestFunctions.matchesOrDoesNotMatch;
import static org.assertj.core.api.Assertions.assertThat;

class FixedUuidSeriesGeneratorTest extends SimpleScenarioTest<FixedUuidSeriesGeneratorTest.State> {
    @Test
    void UUIDs_generated_have_expected_values() {
        when().generating_$_UUIDs(5);
        then().the_UUID_at_index_$n_is_$_when_represented_as_text(0, "00000000-1111-2222-3333-444444444444");
        then().the_UUID_at_index_$n_is_$_when_represented_as_text(1, "00000001-1111-2222-3333-444444444444");
        then().the_UUID_at_index_$n_is_$_when_represented_as_text(2, "00000002-1111-2222-3333-444444444444");
        then().the_UUID_at_index_$n_is_$_when_represented_as_text(3, "00000003-1111-2222-3333-444444444444");
        then().the_UUID_at_index_$n_is_$_when_represented_as_text(4, "00000004-1111-2222-3333-444444444444");
    }
    @Test
    void UUIDs_generated_are_formatted_with_sequence_of_integers_starting_at_0() {
        when().generating_$_UUIDs(10);
        then().the_width_of_the_index_part_is_$_in_the_zero_based_series_of_$_UUIDs_that_was_created(8, 10);
    }

    @Test
    void UUIDs_generated_are_unique() {
        when().generating_$_UUIDs(3);
        then().UUID_$_$_UUID_$(0, "matches", 0)
                .and().UUID_$_$_UUID_$(1, "matches", 1)
                .and().UUID_$_$_UUID_$(2, "matches", 2)
                .but()
                .UUID_$_$_UUID_$(0, "does not match", 1)
                .UUID_$_$_UUID_$(2, "does not match", 0)
                .UUID_$_$_UUID_$(1, "does not match", 0);
    }


    static class State extends Stage<State> {
        private final FixedUuidSeriesGenerator sut = new FixedUuidSeriesGenerator();
        private final List<UUID> uuids = new ArrayList<>();

        State generating_$_UUIDs(int count) {
            uuids.addAll(IntStream.range(0, count).mapToObj(i -> sut.generate()).collect(Collectors.toList()));
            return self();
        }

        State the_width_of_the_index_part_is_$_in_the_zero_based_series_of_$_UUIDs_that_was_created(int indexStringWidth, int untilIndex) {
            assertThat(uuids.size()).isEqualTo(untilIndex);
            String format = "%0" + indexStringWidth + "d";
            for (int i = 0; i < uuids.size(); i++) {
                String expectedUuidPrefix = String.format(format, i);
                String actualUuidPrefix = uuids.get(i).toString().substring(0, 8);
                assertThat(actualUuidPrefix).isEqualTo(expectedUuidPrefix);
            }
            return self();
        }

        State UUID_$_$_UUID_$(int index1, String matchesOrDoesNotMatch, int index2) {
            UUID uuid1 = uuids.get(index1);
            UUID uuid2 = uuids.get(index2);
            if (matchesOrDoesNotMatch(matchesOrDoesNotMatch)) {
                assertThat(FixedUuidSeriesGenerator.matchesInFirst8Positions(uuid1, uuid2)).isTrue();
            } else {
                assertThat(FixedUuidSeriesGenerator.matchesInFirst8Positions(uuid1, uuid2)).isFalse();
            }
            return self();
        }

        State the_UUID_at_index_$n_is_$_when_represented_as_text(int uuidIndex, @Quoted String expectedUuidAsString) {
            assertThat(uuids.get(uuidIndex)).hasToString(expectedUuidAsString);
            return self();
        }
    }
}
