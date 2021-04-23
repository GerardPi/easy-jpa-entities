package com.github.gerardpi.easy.jpaentities.test1;

import java.util.stream.Stream;

public enum Lang {
    DA,
    DE,
    EN,
    ES,
    FR,
    IT,
    NL,
    SE,
    JA,
    PT_BR("pt-BR");

    private final String ietfLangTag;

    Lang() {
        this(null);
    }
    Lang(String ietfLangTag) {
        this.ietfLangTag = ietfLangTag;
    }

    public static Lang fromString(String value) {
        return Stream.of(values())
                .filter(lang -> lang.match(value))
                .findAny().orElseThrow(() -> new IllegalArgumentException("No language available for '" + value + "'"));
    }

    private boolean match(String code) {
        return name().equalsIgnoreCase(code) || code.equals(ietfLangTag);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
