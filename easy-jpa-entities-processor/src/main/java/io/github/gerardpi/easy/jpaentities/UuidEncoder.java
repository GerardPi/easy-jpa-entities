package io.github.gerardpi.easy.jpaentities;

import java.util.Base64;
import java.util.UUID;

/**
 * This class can encode and decode a UUID to a shorter form.
 * The default UUID toString method is 32 characters.
 * This encoding produces a length of 22 characters.
 */
public class UuidEncoder {
    public static final int EXPECTED_LENGTH_UUID_AS_STRING = 22;
    public static final String ERROR_MESSAGE_INVALID_LENGTH = "Invalid length.";
    private static final int EXPECTED_UUID_AS_BYTES_COUNT = 16;
    private static final int INDEX_START_MSB = 0;
    private static final int INDEX_STOP_MSB = 8;
    private static final int INDEX_START_LSB = INDEX_STOP_MSB;
    private static final int INDEX_STOP_LSB = 16;

    private static byte[] asByteArray(UUID uuid) {
        byte[] buffer = new byte[16];
        copyToBuffer(uuid.getMostSignificantBits(), buffer, 0);
        copyToBuffer(uuid.getLeastSignificantBits(), buffer, 8);
        return buffer;
    }

    private static void copyToBuffer(long bits, byte[] buffer, int startIndex) {
        for (int i = startIndex; i < startIndex + 8; i++) {
            buffer[i] = (byte) (bits >>> 8 * (7 - i));
        }
    }

    private static UUID toUuid(byte[] uuidBytes) {
        if (uuidBytes.length != EXPECTED_UUID_AS_BYTES_COUNT) {
            throw new IllegalArgumentException("Expected " + EXPECTED_UUID_AS_BYTES_COUNT + " bytes, but got: " + uuidBytes.length);
        }
        return new UUID(
                filterBytes(uuidBytes, INDEX_START_MSB, INDEX_STOP_MSB),
                filterBytes(uuidBytes, INDEX_START_LSB, INDEX_STOP_LSB));
    }

    private static long filterBytes(byte[] byteArray, int startIndex, int stopIndex) {
        long value = 0;
        for (int i = startIndex; i < stopIndex; i++) {
            value = (value << 8) | (byteArray[i] & 0xff);
        }
        return value;
    }

    public String encode(UUID uuid) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(asByteArray(uuid));
    }

    public UUID decode(String uuidAsString) {
        if (uuidAsString.length() != EXPECTED_LENGTH_UUID_AS_STRING) {
            throw new IllegalArgumentException(ERROR_MESSAGE_INVALID_LENGTH + " The base64 encoded string '" + uuidAsString +
                    "' must have a length of " + EXPECTED_LENGTH_UUID_AS_STRING + " characters.");
        }
        return toUuid(Base64.getUrlDecoder().decode(uuidAsString));
    }
}
