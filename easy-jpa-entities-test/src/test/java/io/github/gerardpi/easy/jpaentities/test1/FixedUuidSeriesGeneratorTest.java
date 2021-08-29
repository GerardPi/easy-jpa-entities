package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class FixedUuidSeriesGeneratorTest extends SimpleScenarioTest<FixedUuidSeriesGeneratorTest.State> {
    @Test
    void test_generated_UUID_has_expected_value() {
        when().generating_$_UUIDs(3);
        then().the_UUID_at_index_$n_is_$(0, "00000000-1111-2222-3333-444444444444");
        then().the_UUID_at_index_$n_is_$(1, "00000001-1111-2222-3333-444444444444");
        then().the_UUID_at_index_$n_is_$(2, "00000002-1111-2222-3333-444444444444");
    }
    @Test
    void test() {
        when().generating_$_UUIDs(10);
        then().the_width_of_the_index_part_is_$_in_the_zero_based_series_of_$_UUIDs_that_was_created(8, 10);
    }

    @Test
    void test_match() {
        when().generating_$_UUIDs(2);
        then().matching_UUID_$_with_$_$(0, 0, "is equal")
                .but()
                .matching_UUID_$_with_$_$(0, 1, "is not equal");
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

        State matching_UUID_$_with_$_$(int index1, int index2, String isOrIsNotEqual) {
            UUID uuid1 = uuids.get(index1);
            UUID uuid2 = uuids.get(index2);
            boolean expectedResult = "is equal".equals(isOrIsNotEqual) ? true : false;
            if (expectedResult) {
                assertThat(FixedUuidSeriesGenerator.matchesInFirst8Positions(uuid1, uuid2)).isTrue();
            } else {
                assertThat(FixedUuidSeriesGenerator.matchesInFirst8Positions(uuid1, uuid2)).isFalse();
            }
            return self();
        }

        State the_UUID_at_index_$n_is_$(int uuidIndex, String expectedUuidAsString) {
            assertThat(uuids.get(uuidIndex)).hasToString(expectedUuidAsString);
            return self();
        }
    }
}
