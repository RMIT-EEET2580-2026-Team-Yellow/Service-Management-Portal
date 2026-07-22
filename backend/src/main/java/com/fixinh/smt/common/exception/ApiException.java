package com.fixinh.smt.common.exception;

import org.springframework.http.HttpStatus;

/** Base unchecked exception any module may throw; carries the HTTP status to render. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
