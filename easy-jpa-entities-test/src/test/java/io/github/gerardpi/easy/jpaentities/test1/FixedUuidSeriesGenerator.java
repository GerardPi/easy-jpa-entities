package io.github.gerardpi.easy.jpaentities.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class implements a generator of a predictable series of UUIDs.
 * Totally pointless (because a UUID should not be predictable) unless you're trying to test something.
 * It generates a UUID that starts with index 00000000 in the first part of the UUID.
 * That number is incremented at each new generation.
 * The rest of the UUID won't ever change.
 */
public class FixedUuidSeriesGenerator implements UuidGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(FixedUuidSeriesGenerator.class);

    private final AtomicInteger counter = new AtomicInteger(0);
    public static final String UUID_SUFFIX = "1111-2222-3333-444444444444";
    @Override
    public UUID generate() {
        return generateWith(counter.getAndIncrement());
    }

    public static boolean matchesInFirst8Positions(UUID uuid1, UUID uuid2) {
        if (uuid1 == uuid2) {
            return true;
        }
        if (uuid1 == null || uuid2 == null) {
            return false;
        }
        String first8_1 = uuid1.toString().substring(0, 8);
        String first8_2 = uuid2.toString().substring(0, 8);
        return first8_1.equals(first8_2);
    }
    public void reset() {
        LOG.info("{} reset to 0", getClass().getName());
        counter.set(0);
    }

    public static UUID generateWith(int counter) {
        return UUID.fromString(String.format("%08d-%s", counter, UUID_SUFFIX));
    }
}
