package com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.HelpDeskExceptionBase;
import org.springframework.http.HttpStatus;

public class TicketPriorityCantBeChanged extends HelpDeskExceptionBase {
    public TicketPriorityCantBeChanged(){
        super("Ticket priority can't be changed", HttpStatus.CONFLICT);
    }
}
