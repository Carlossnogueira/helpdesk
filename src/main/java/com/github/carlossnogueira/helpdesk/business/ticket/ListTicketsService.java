package com.github.carlossnogueira.helpdesk.business.ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsResponse;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketPageResponse;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ListTicketsService {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private TicketRepository ticketRepository;

    public TicketPageResponse execute(int page, UserDetail userDetail) {

        boolean isAdminOrSupport = userDetail.role().equals("ADMIN") || userDetail.role().equals("SUPPORT");
        Page<Ticket> ticketsPage;

        if (isAdminOrSupport) {
            ticketsPage = ticketRepository.findAll(PageRequest.of(page, PAGE_SIZE));
        } else {
            ticketsPage = ticketRepository.findAllByUserId(userDetail.id(), PageRequest.of(page, PAGE_SIZE));
        }


        return TicketPageResponse.builder()
                .page(ticketsPage.getNumber())
                .TotalPages(ticketsPage.getTotalPages())
                .size(ticketsPage.getSize())
                .lastPage(ticketsPage.isLast())
                .tickets(
                        ticketsPage.getContent()
                                .stream()
                                .map(ticket -> TicketDetailsResponse.builder()
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

