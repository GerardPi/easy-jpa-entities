package io.github.gerardpi.easy.jpaentities.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class TestDateTimeSupplier implements Supplier<OffsetDateTime> {
    private static final Logger LOG = LoggerFactory.getLogger(TestDateTimeSupplier.class);

    private Clock fixedClock;

    public TestDateTimeSupplier() {
        this.fixedClock = null;
    }

    @Override
    public OffsetDateTime get() {
        if (fixedClock != null) {
            OffsetDateTime dateTime = OffsetDateTime.now(fixedClock);
            LOG.info("Returning fixed date time: '" + dateTime + "'");
            return dateTime;
        }
        OffsetDateTime dateTime = OffsetDateTime.now();
        LOG.info("No fixed date time was set. Returned value: '" + dateTime + "'");
        return dateTime;
    }

    public void fixDateTime(@Nonnull OffsetDateTime fixedDateTime) {
        String previousDateTime = getPreviousDateTimeDisplayValue();
        this.fixedClock = Clock.fixed(requireNonNull(fixedDateTime).toInstant(),
                fixedDateTime.getOffset());
        LOG.info("Fixed date time was '" + previousDateTime + "' and is now '" + OffsetDateTime.now(fixedClock) + "'");
    }

    public void clearFixedDateTime() {
        if (fixedClock != null) {
            String previousDateTime = getPreviousDateTimeDisplayValue();
            LOG.info("Fixed date time was '" + previousDateTime + "' and is now cleared.");
        }
        this.fixedClock = null;
    }

    private String getPreviousDateTimeDisplayValue() {
        return this.fixedClock == null ? "not set" : OffsetDateTime.now(fixedClock).toString();
    }
}
