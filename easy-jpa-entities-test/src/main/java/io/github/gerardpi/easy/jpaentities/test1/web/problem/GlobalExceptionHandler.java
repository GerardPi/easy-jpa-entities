package io.github.gerardpi.easy.jpaentities.test1.web.problem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    HttpEntity<RestApiMessageDto> handleIllegalArgumentException(Throwable e, HttpServletRequest request) {
        return handleException(e, request, "invalid arguments", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    HttpEntity<RestApiMessageDto> handleMethodArgTypeMismatchException(Throwable e, HttpServletRequest request) {
        return handleException(e, request, "corrupted arguments", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    HttpEntity<RestApiMessageDto> handleNoSuchElementException(Throwable e, HttpServletRequest request) {
        return handleException(e, request, "item was not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityTagMismatchException.class)
    HttpEntity<RestApiMessageDto> handjeEntityTagMismatchException(Throwable e, HttpServletRequest request) {
        return handleException(e, request, "item was not in expected state", HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(EntityTagValueMissingException.class)
    HttpEntity<RestApiMessageDto> handleEntityTagValueMissingException(Throwable e, HttpServletRequest request) {
        return handleException(e, request, "A " + HttpHeaders.ETAG + " value is required to be sent in the request in the " + HttpHeaders.IF_MATCH + " header.",
                HttpStatus.PRECONDITION_REQUIRED);
    }

    @ExceptionHandler(EntityNotModifiedException.class)
    HttpEntity<RestApiMessageDto> handleEntityNotModifiedException(Throwable e, HttpServletRequest request) {
        return handleException(e, request, "The value of the " + HttpHeaders.IF_NONE_MATCH + " header was the same as the current version of the entity requested.",
                HttpStatus.NOT_MODIFIED);
    }

    private <T extends Throwable> HttpEntity<RestApiMessageDto> handleException(T throwable, HttpServletRequest request, String title, HttpStatus httpStatus) {
        RestApiMessageDto error = RestApiMessageDto.create()
                .setTitle(title)
                .setPath(request.getPathInfo())
                .setMethod(request.getMethod())
                .setStatusCode(httpStatus.value())
                .setStatusName(httpStatus.getReasonPhrase())
                .setStatusSeries(httpStatus.series().name())
                .setTimestamp(OffsetDateTime.now())
                .setTraceId("" + OffsetDateTime.now().toInstant().toEpochMilli())
                .build();
        return ResponseEntity
                .status(error.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(error);
    }
}
