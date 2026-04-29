package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.business.GenericMessageResponse;
import com.github.carlossnogueira.helpdesk.business.dto.ticket.*;
import com.github.carlossnogueira.helpdesk.business.ticket.*;
import com.github.carlossnogueira.helpdesk.documentation.DefaultErrorResponses;
import com.github.carlossnogueira.helpdesk.documentation.DeniedAccessResponses;
import com.github.carlossnogueira.helpdesk.documentation.ResourceNotFoundResponses;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    private GetTicketByIdService getTicketByIdService;

    @Autowired
    private ListTicketsService listTicketsService;

    @Autowired
    private UpdateTicketService updateTicketService;

    @Autowired
    private DeleteTicketService deleteTicketService;

    @Autowired
    private UpdateTicketStatusService updateTicketStatusService;

    @Autowired
    private SetTicketPriorityService setTicketPriorityService;

    @Operation(summary = "Create a new support ticket")
    @ApiResponse(responseCode = "200", description = "Returns a success message with the ID of the created ticket")
    @ResourceNotFoundResponses
    @DefaultErrorResponses
    @PostMapping("/tickets")
    public ResponseEntity<GenericMessageResponse> createTicket(@Valid @RequestBody TicketDto ticketDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        var result = registerTicketService.registerTicket(ticketDto, userDetail.id());
        return ResponseEntity.ok().body(new GenericMessageResponse("Ticket created successfully with ID: " + result.getId()));
    }

    @Operation(summary = "List support tickets with pagination")
    @ApiResponse(responseCode = "200", description = "Returns a paginated list of support tickets")
    @DeniedAccessResponses
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @GetMapping("/tickets")
    public ResponseEntity<TicketPageResponse> listTickets(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(listTicketsService.execute(page));
    }

    @Operation(summary = "Get details of a specific support ticket by ID")
    @ApiResponse(responseCode = "200", description = "Returns the details of the specified support ticket")
    @DeniedAccessResponses
    @ResourceNotFoundResponses
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketDetailsResponse> getById(@PathVariable long id) {
        return ResponseEntity.ok(getTicketByIdService.execute(id));
    }

    @Operation(summary = "Update an existing support ticket")
    @ApiResponse(responseCode = "200", description = "Returns the updated support ticket details")
    @DeniedAccessResponses
    @ResourceNotFoundResponses
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/tickets/{id}")
    public ResponseEntity<TicketDetailsResponse> updateTicket(@PathVariable long id,
                                                              @RequestBody TicketUpdateDto dto) {
        return ResponseEntity.ok(updateTicketService.execute(id, dto));
    }

    @Operation(summary = "Update status of an existing Ticket")
    @ApiResponse(responseCode = "200", description = "Returns the updated support ticket details with new status")
    @DeniedAccessResponses
    @ResourceNotFoundResponses
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @PatchMapping("/tickets/{id}/status")
    public ResponseEntity<TicketDetailsResponse> updateStatus(@PathVariable long id,
                                                              @Valid @RequestBody TicketStatusUpdateRequest dto) {
        return ResponseEntity.ok(updateTicketStatusService.execute(id, dto));
    }


    @Operation(summary = "Delete an existing Ticket")
    @ApiResponse(responseCode = "204", description = "Support ticket deleted successfully")
    @DeniedAccessResponses
    @ResourceNotFoundResponses
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable long id) {
        deleteTicketService.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change Ticket priority")
    @ApiResponse(responseCode = "200", description = "Returns the updated support ticket details with new priority")
    @DeniedAccessResponses
    @ResourceNotFoundResponses
    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMIN')")
    @PatchMapping("/tickets/{id}/priority")
    public ResponseEntity<TicketDetailsResponse> updatePriority(@PathVariable long id,
                                                              @Valid @RequestBody TicketPriorityUpdateRequest dto) {
        return ResponseEntity.ok(setTicketPriorityService.execute(id, dto));
    }

}
