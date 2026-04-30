package com.github.carlossnogueira.helpdesk.infrastructure.exception.user;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class CurrentPasswordIncorrectException extends HelpDeskExceptionBase {
    public CurrentPasswordIncorrectException() {
        super("Current password is incorrect", HttpStatus.BAD_REQUEST);
    }
}

