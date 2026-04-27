package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.business.GenericMessageDto;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.*;
import com.github.carlossnogueira.helpdesk.business.ticket.*;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TicketController {

    @Autowired
    private RegisterTicketService registerTicketService;

    @Autowired
    private GetTicketByIdService getTicketByIdService;

    @Autowired
    private ListTicketsService listTicketsService;

    @Autowired
    private UpdateTicketService updateTicketService;

    @Autowired
    private DeleteTicketService deleteTicketService;

    @Autowired
    private UpdateTicketStatusService updateTicketStatusService;

    @PostMapping("/tickets")
    public ResponseEntity<GenericMessageDto> createTicket(@Valid @RequestBody TicketDto ticketDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        var result = registerTicketService.registerTicket(ticketDto, userDetail.id());
        return ResponseEntity.ok().body(new GenericMessageDto("Ticket created successfully with ID: " + result.getId()));
    }

    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @GetMapping("/tickets")
    public ResponseEntity<TicketPageResponse> listTickets(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(listTicketsService.execute(page));
    }

    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketDetailsDto> getById(@PathVariable long id) {
        return ResponseEntity.ok(getTicketByIdService.execute(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/tickets/{id}")
    public ResponseEntity<TicketDetailsDto> updateTicket(@PathVariable long id,
                                                         @RequestBody TicketUpdateDto dto) {
        return ResponseEntity.ok(updateTicketService.execute(id, dto));
    }

    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @PatchMapping("/tickets/{id}/status")
    public ResponseEntity<TicketDetailsDto> updateStatus(@PathVariable long id,
                                                         @Valid @RequestBody TicketStatusUpdateDto dto) {
        return ResponseEntity.ok(updateTicketStatusService.execute(id, dto));
    }

    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable long id) {
        deleteTicketService.execute(id);
        return ResponseEntity.noContent().build();
    }

}
