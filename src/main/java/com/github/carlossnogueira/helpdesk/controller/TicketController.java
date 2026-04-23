package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.business.GenericMessageDto;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDto;
import com.github.carlossnogueira.helpdesk.business.user.RegisterTicketService;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TicketController {

    @Autowired
    private RegisterTicketService registerTicketService;

    @PostMapping("/tickets")
    public ResponseEntity<GenericMessageDto> createTicket(@Valid @RequestBody TicketDto ticketDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        var result = registerTicketService.registerTicket(ticketDto, userDetail.id());
        return ResponseEntity.ok().body(new GenericMessageDto("Ticket created successfully with ID: " + result.getId()));
    }

}
