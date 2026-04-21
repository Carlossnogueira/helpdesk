package com.github.carlossnogueira.helpdesk.infrastructure.exception.user;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class EmailOrPasswordIncorrectException extends HelpDeskExceptionBase {
    public EmailOrPasswordIncorrectException() {
        super("User email or password is incorrect", HttpStatus.NOT_FOUND);
    }
}
