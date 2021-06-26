package io.github.gerardpi.easy.jpaentities.test1.domain;


import com.google.common.base.Strings;

import java.util.Optional;
import java.util.stream.Stream;

public enum ItemType {
    MUSIC_COMPACT_CASETTE("compact-casette"),
    MUSIC_COMPACT_DISC("compact-disc"),
    ORIGINAL_WORK("original-work"),
    GAME_DVD("game-dvd"),
    GAME_CARTRIDGE("game-cartridge"),
    HOME_VIDEO_DVD("dvd"),
    HOME_VIDEO_VHS("vhs"),
    HOME_VIDEO_BLU_RAY("blr"),
    HOME_VIDEO_BUNDLE("home-video-bundle");

    private final String code;

    ItemType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Optional<ItemType> fromCode(String code) {
        return Stream.of(values())
                .filter(type -> type.code.equals(code))
                .findAny();
    }

    public static ItemType convertFromCode(String code) {
        return fromCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Can not convert '" + code + "' into " + ItemType.class.getSimpleName()));
    }

    /**
     * @return True when typePrefix is not null or empty and if it is a prefix of one or more of the types.
     * Is case sensitive.
     */
    public static boolean isTypePrefix(String typePrefix) {
        if (Strings.isNullOrEmpty(typePrefix)) {
            return false;
        }
        return Stream.of(values())
                .anyMatch(type -> type.name().startsWith(typePrefix));
    }
}
