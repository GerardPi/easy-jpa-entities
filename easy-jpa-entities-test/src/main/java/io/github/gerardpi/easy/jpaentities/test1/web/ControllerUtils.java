package io.github.gerardpi.easy.jpaentities.test1.web;

import io.github.gerardpi.easy.jpaentities.test1.domain.EntityDtoWithTag;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersistableEntityWithTag;
import io.github.gerardpi.easy.jpaentities.test1.web.problem.EntityNotModifiedException;
import io.github.gerardpi.easy.jpaentities.test1.web.problem.EntityTagMismatchException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class ControllerUtils {
    public static void assertEtagDifferent(Optional<String> ifNoneMatchHeader, String currentEtagValue, String path) {
        ifNoneMatchHeader.ifPresent(eTag -> {
            if (currentEtagValue.equalsIgnoreCase(eTag)) {
                throw new EntityNotModifiedException("The version " + eTag + " is the current version of " + path);
            }
        });
    }
    public static <T extends PersistableEntityWithTag> void assertEtagEqual(T entity, int expectedEtagValue) {
        if (entity.getEtag() == null) {
            throw new IllegalStateException("Field " + PersistableEntityWithTag.PROPNAME_ETAG + " was not set. This must never happen!");
        }
        if (!entity.getEtag().equals(expectedEtagValue)) {
            throw new EntityTagMismatchException("The entity " + entity.getClass().getSimpleName() + " to update with ID " + entity.getId()
                    + " does not have expected etag " + expectedEtagValue
                    + " (actual etag = " + entity.getEtag() + ").");
        }
    }
    public static <T extends EntityDtoWithTag> HttpEntity<T> okResponse(T entityDto) {
        return ResponseEntity.ok()
                .cacheControl(cacheForOneMinute())
                .eTag(entityDto.getEtag())
                .lastModified(ZonedDateTime.now())
                .body(entityDto);
    }

    public static HttpEntity<Void> okNoContent() {
        return ResponseEntity.noContent().build();
    }

    public static CacheControl cacheForOneMinute() {
        return CacheControl.maxAge(1, TimeUnit.MINUTES);
    }
    private ControllerUtils() {
        // No instantiation allowed/
    }

    public static URI toUri(String prefix, String suffix) {
        return URI.create(prefix + "/" + suffix);
    }
}
