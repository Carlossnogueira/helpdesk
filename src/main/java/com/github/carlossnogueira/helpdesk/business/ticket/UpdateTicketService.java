package com.github.carlossnogueira.helpdesk.business.ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsDto;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketUpdateDto;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketAlreadyClosed;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateTicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public TicketDetailsDto execute(long id, TicketUpdateDto dto) {
        var ticket = ticketRepository.findById(id)
                .orElseThrow(TicketNotFoundException::new);

        if(ticket.getStatus() == Status.CLOSED)
            throw new TicketAlreadyClosed();

        ticket.setTitle(dto.getTitle().isEmpty() ? ticket.getTitle() : dto.getTitle());
        ticket.setDescription(dto.getDescription().isEmpty() ? ticket.getDescription() : dto.getDescription());

        var saved = ticketRepository.save(ticket);


        return TicketDetailsDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .status(saved.getStatus())
                .priority(saved.getPriority())
                .createdAt(saved.getCreatedAt())
                .calledBy(saved.getUser().getName())
                .calledByEmail(saved.getUser().getEmail())
                .build();


    }

}

