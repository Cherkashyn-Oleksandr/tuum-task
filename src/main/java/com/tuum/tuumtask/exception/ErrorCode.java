package com.tuum.tuumtask.exception;

import org.springframework.http.HttpStatus;

//enum for error codes
public enum ErrorCode {

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Account not found"),

    INVALID_CURRENCY(HttpStatus.BAD_REQUEST, "Invalid currency"),
    INVALID_DIRECTION(HttpStatus.BAD_REQUEST, "Invalid direction"),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "Invalid amount"),
    DESCRIPTION_MISSING(HttpStatus.BAD_REQUEST, "Description missing"),
    INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "Insufficient funds");

    private final HttpStatus status;
    private final String message;

    //construct
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    //http status
    public HttpStatus getStatus() {
        return status;
    }

    //error message
    public String getMessage() {
        return message;
    }
}
