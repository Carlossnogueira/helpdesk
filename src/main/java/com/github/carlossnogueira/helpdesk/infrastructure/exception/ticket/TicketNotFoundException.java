package com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class TicketNotFoundException extends HelpDeskExceptionBase {

    public TicketNotFoundException(){
        super("Ticket not found", HttpStatus.NOT_FOUND);
    }

}
