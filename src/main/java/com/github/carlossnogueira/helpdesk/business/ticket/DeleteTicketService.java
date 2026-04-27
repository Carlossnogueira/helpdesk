package com.github.carlossnogueira.helpdesk.business.ticket;

import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteTicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public void execute(long id) {
        var ticket = ticketRepository.findById(id)
                .orElseThrow(TicketNotFoundException::new);

        ticketRepository.delete(ticket);
    }

}

