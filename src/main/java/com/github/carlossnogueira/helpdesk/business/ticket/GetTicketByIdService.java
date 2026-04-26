package com.github.carlossnogueira.helpdesk.business.ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsDto;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetTicketByIdService {

    @Autowired
    private TicketRepository ticketRepository;

    public TicketDetailsDto execute(long id){
        var ticket = ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException());

        return new TicketDetailsDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCreatedAt(),
                ticket.getUser().getName(),
                ticket.getUser().getEmail()
        );

    }

}
