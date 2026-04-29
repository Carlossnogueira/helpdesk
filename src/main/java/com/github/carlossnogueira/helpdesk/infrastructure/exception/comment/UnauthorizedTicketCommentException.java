package com.github.carlossnogueira.helpdesk.infrastructure.exception.comment;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class UnauthorizedTicketCommentException extends HelpDeskExceptionBase {

    public UnauthorizedTicketCommentException() {
        super("You are not authorized to interact with comments on this ticket", HttpStatus.FORBIDDEN);
    }

}

