package com.tuum.tuumtask.exception;

import java.time.Instant;

public class ErrorResponse {

    private final String message;
    private final Instant timestamp;
    private final String error;

    public ErrorResponse(String message, Instant timestamp, String error) {
        this.message = message;
        this.timestamp = timestamp;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }
}
