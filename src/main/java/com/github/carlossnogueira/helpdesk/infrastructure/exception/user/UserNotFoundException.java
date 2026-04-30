package com.github.carlossnogueira.helpdesk.infrastructure.exception.user;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends HelpDeskExceptionBase {
    public UserNotFoundException() {
        super("User not found", HttpStatus.NOT_FOUND);
    }
}

