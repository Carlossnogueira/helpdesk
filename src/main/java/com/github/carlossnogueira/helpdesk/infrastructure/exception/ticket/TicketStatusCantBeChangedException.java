package com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class TicketStatusCantBeChangedException extends HelpDeskExceptionBase {
    public TicketStatusCantBeChangedException() {
        super("The ticket status can't be changed to the specified value.", HttpStatus.CONFLICT);
    }
}
