package io.github.gerardpi.easy.jpaentities.test1.web.problem;

import java.time.OffsetDateTime;
import java.util.List;

public class RestApiMessageDto {
    private final String path;
    private final String method;
    private final int statusCode;
    private final String statusName;
    private final String statusSeries;
    private final String title;
    private final List<String> messages;
    private final OffsetDateTime timestamp;
    private final String traceId;

    private RestApiMessageDto(Builder builder) {
        this.path = builder.path;
        this.method = builder.method;
        this.statusCode = builder.statusCode;
        this.statusName = builder.statusName;
        this.statusSeries = builder.statusSeries;
        this.title = builder.title;
        this.messages = builder.messages;
        this.timestamp = builder.timestamp;
        this.traceId = builder.traceId;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getStatusSeries() {
        return statusSeries;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getMessages() {
        return messages;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public static Builder create() {
        return new Builder();
    }

    static class Builder {
        private String path;
        private String method;
        private int statusCode;
        private String statusName;
        private String statusSeries;
        private String title;
        private List<String> messages;
        private OffsetDateTime timestamp;
        private String traceId;

        private Builder() {
            // Use create method in parent class.
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder setStatusName(String statusName) {
            this.statusName = statusName;
            return this;
        }

        public Builder setStatusSeries(String statusSeries) {
            this.statusSeries = statusSeries;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessages(List<String> messages) {
            this.messages = messages;
            return this;
        }

        public Builder setTimestamp(OffsetDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setTraceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public RestApiMessageDto build() {
            return new RestApiMessageDto(this);
        }
    }
}
