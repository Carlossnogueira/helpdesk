package com.github.carlossnogueira.helpdesk.business.ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsResponse;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketPriorityUpdateRequest;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketPriorityCantBeChanged;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetTicketPriorityService {

    @Autowired
    private TicketRepository ticketRepository;

    public TicketDetailsResponse execute(long id, TicketPriorityUpdateRequest ticketPriorityUpdateRequest) {
        var ticket = ticketRepository.findById(id).orElseThrow(TicketNotFoundException::new);

        if(!ticketPriorityUpdateRequest.getPriority().equals(Priority.HIGH.name()) &&
                !ticketPriorityUpdateRequest.getPriority().equals(Priority.MEDIUM.name()) &&
                !ticketPriorityUpdateRequest.getPriority().equals(Priority.LOW.name())) {
            throw new TicketPriorityCantBeChanged();
        }

        if (ticketPriorityUpdateRequest.getPriority().equals(Priority.NOT_MEASURED.name())){
            throw new  TicketPriorityCantBeChanged();
        }

        ticket.setPriority(Priority.valueOf(ticketPriorityUpdateRequest.getPriority()));
        ticketRepository.save(ticket);

        return TicketDetailsResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdAt(ticket.getCreatedAt())
                .calledBy(ticket.getUser().getName())
                .calledByEmail(ticket.getUser().getEmail())
                .build();

    }
}
