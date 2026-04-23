package com.github.carlossnogueira.helpdesk.business.user;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDto;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterTicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    public Ticket registerTicket(TicketDto ticketDto, long userId) {

        var userReference = userRepository.getReferenceById(userId);

        var ticket = Ticket.builder()
                .title(ticketDto.getTitle())
                .description(ticketDto.getDescription())
                .user(userReference)
                .build();

        return  ticketRepository.save(ticket);
    }

}
