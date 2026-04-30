package com.github.carlossnogueira.helpdesk.Ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketStatusUpdateRequest;
import com.github.carlossnogueira.helpdesk.business.ticket.UpdateTicketStatusService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketStatusCantBeChangedException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateTicketStatusServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private UpdateTicketStatusService updateTicketStatusService;

    private Ticket buildTicket(Status status) {
        var user = User.builder().id(1L).name("Alice").email("alice@example.com").build();
        return Ticket.builder()
                .id(1L)
                .title("Issue")
                .description("Some issue description here")
                .status(status)
                .priority(Priority.NOT_MEASURED)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Test
    void shouldUpdateStatusToOpen() {
        var ticket = buildTicket(Status.IN_PROGRESS);
        var dto = new TicketStatusUpdateRequest();
        dto.setStatus(Status.OPEN.name());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        var result = updateTicketStatusService.execute(1L, dto);

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldUpdateStatusToClosed() {
        var ticket = buildTicket(Status.IN_PROGRESS);
        var dto = new TicketStatusUpdateRequest();
        dto.setStatus(Status.CLOSED.name());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        var result = updateTicketStatusService.execute(1L, dto);

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldThrowExceptionWhenStatusIsInProgress() {
        var dto = new TicketStatusUpdateRequest();
        dto.setStatus(Status.IN_PROGRESS.name());

        assertThrows(TicketStatusCantBeChangedException.class, () ->
                updateTicketStatusService.execute(1L, dto));

        verify(ticketRepository, never()).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenTicketIsAlreadyClosed() {
        var ticket = buildTicket(Status.CLOSED);
        var dto = new TicketStatusUpdateRequest();
        dto.setStatus(Status.OPEN.name());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(TicketStatusCantBeChangedException.class, () ->
                updateTicketStatusService.execute(1L, dto));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        var dto = new TicketStatusUpdateRequest();
        dto.setStatus(Status.OPEN.name());

        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                updateTicketStatusService.execute(99L, dto));
    }
}

