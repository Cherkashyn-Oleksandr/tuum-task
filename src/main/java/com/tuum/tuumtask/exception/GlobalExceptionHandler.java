package com.tuum.tuumtask.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Handle all errors
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex
    ) {
        ErrorCode code = ex.getErrorCode();

        //http response
        return ResponseEntity
                .status(code.getStatus())
                .body(new ErrorResponse(
                        code.getMessage(),
                        Instant.now(),
                        code.name()
                ));
    }
}
