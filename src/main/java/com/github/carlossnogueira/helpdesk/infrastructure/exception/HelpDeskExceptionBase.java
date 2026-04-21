package com.github.carlossnogueira.helpdesk.infrastructure.exception;

import org.springframework.http.HttpStatus;

public abstract class HelpDeskExceptionBase extends RuntimeException {

    private final HttpStatus httpStatus;

    public HelpDeskExceptionBase(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
         return httpStatus;
    }
}
