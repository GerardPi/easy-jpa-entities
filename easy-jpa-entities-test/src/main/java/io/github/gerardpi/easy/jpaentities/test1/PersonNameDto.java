package io.github.gerardpi.easy.jpaentities.test1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonNameDto {
    private final String first;
    private final String last;
    @JsonCreator
    PersonNameDto(@JsonProperty("first") String first, @JsonProperty("last") String last) {
        this.first = first;
        this.last = last;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }
}
