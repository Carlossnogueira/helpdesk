package com.github.carlossnogueira.helpdesk.business.ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsResponse;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetTicketByIdService {

    @Autowired
    private TicketRepository ticketRepository;

    public TicketDetailsResponse execute(long id, UserDetail userDetail) {
        var ticket = ticketRepository.findById(id).orElseThrow(TicketNotFoundException::new);

        if(userDetail.id() != ticket.getUser().getId() && userDetail.role().equals("USER")) {
            throw new TicketNotFoundException();
        }

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
