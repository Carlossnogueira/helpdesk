package com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class TicketAlreadyClosed extends HelpDeskExceptionBase {
    public TicketAlreadyClosed() {
        super("Ticket is closed", HttpStatus.CONFLICT);
    }
}
