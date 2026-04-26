package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.business.GenericMessageDto;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsDto;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDto;
import com.github.carlossnogueira.helpdesk.business.ticket.GetTicketByIdService;
import com.github.carlossnogueira.helpdesk.business.ticket.RegisterTicketService;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TicketController {

    @Autowired
    private RegisterTicketService registerTicketService;

    @Autowired
    private GetTicketByIdService  getTicketByIdService;

    @PostMapping("/tickets")
    public ResponseEntity<GenericMessageDto> createTicket(@Valid @RequestBody TicketDto ticketDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        var result = registerTicketService.registerTicket(ticketDto, userDetail.id());
        return ResponseEntity.ok().body(new GenericMessageDto("Ticket created successfully with ID: " + result.getId()));
    }

    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketDetailsDto> getById(@PathVariable long id){
        var result = getTicketByIdService.execute(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + authentication);
        System.out.println("Authorities: " + authentication.getAuthorities());

        return ResponseEntity.ok().body(result);
    }


}
