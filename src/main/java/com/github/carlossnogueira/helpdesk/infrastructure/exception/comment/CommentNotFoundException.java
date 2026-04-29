package com.github.carlossnogueira.helpdesk.infrastructure.exception.comment;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends HelpDeskExceptionBase {

    public CommentNotFoundException() {
        super("Comment not found", HttpStatus.NOT_FOUND);
    }

}

