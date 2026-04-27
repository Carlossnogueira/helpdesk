package com.github.carlossnogueira.helpdesk.business.ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsDto;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketPageResponse;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ListTicketsService {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private TicketRepository ticketRepository;

    public TicketPageResponse execute(int page) {

        var tickets = ticketRepository.findAll(PageRequest.of(page, PAGE_SIZE));

        return TicketPageResponse.builder()
                .page(tickets.getNumber())
                .TotalPages(tickets.getTotalPages())
                .size(tickets.getSize())
                .lastPage(tickets.isLast())
                .tickets(
                        tickets.getContent()
                                .stream()
                                .map(ticket -> TicketDetailsDto.builder()
                                        .id(ticket.getId())
                                        .title(ticket.getTitle())
                                        .description(ticket.getDescription())
                                        .status(ticket.getStatus())
                                        .priority(ticket.getPriority())
                                        .createdAt(ticket.getCreatedAt())
                                        .calledBy(ticket.getUser().getName())
                                        .calledByEmail(ticket.getUser().getEmail())
                                        .build()
                                )
                                .toList()
                )
                .build();

    }

}

