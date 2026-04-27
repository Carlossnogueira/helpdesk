package com.github.carlossnogueira.helpdesk.business.ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsDto;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketStatusUpdateDto;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketStatusCantBeChangedException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateTicketStatusService {

    @Autowired
    private TicketRepository ticketRepository;

    public TicketDetailsDto execute(long id, TicketStatusUpdateDto dto) {
        var ticket = ticketRepository.findById(id)
                .orElseThrow(TicketNotFoundException::new);

        if(ticket.getStatus() == Status.IN_PROGRESS && dto.getStatus() == Status.OPEN) {
            throw new TicketStatusCantBeChangedException();
        }

        if(ticket.getStatus() == Status.CLOSED){
            throw new TicketStatusCantBeChangedException();
        }

        ticket.setStatus(dto.getStatus());

        var saved = ticketRepository.save(ticket);

        return new TicketDetailsDto(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStatus(),
                saved.getPriority(),
                saved.getCreatedAt(),
                saved.getUser().getName(),
                saved.getUser().getEmail()
        );
    }

}

