package com.github.carlossnogueira.helpdesk.infrastructure.exception.user;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends HelpDeskExceptionBase {

    public UserAlreadyExistsException() {
        super("User already Exists", HttpStatus.BAD_REQUEST);
    }

}
