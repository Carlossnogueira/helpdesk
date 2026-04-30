package com.github.carlossnogueira.helpdesk.Ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketPriorityUpdateRequest;
import com.github.carlossnogueira.helpdesk.business.ticket.SetTicketPriorityService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketPriorityCantBeChanged;
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
public class SetTicketPriorityServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private SetTicketPriorityService setTicketPriorityService;

    private Ticket buildTicket() {
        var user = User.builder().id(1L).name("Alice").email("alice@example.com").build();
        return Ticket.builder()
                .id(1L)
                .title("Issue")
                .description("Some issue description here")
                .status(Status.OPEN)
                .priority(Priority.NOT_MEASURED)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Test
    void shouldSetPriorityToHigh() {
        var ticket = buildTicket();
        var dto = new TicketPriorityUpdateRequest();
        dto.setPriority(Priority.HIGH.name());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        var result = setTicketPriorityService.execute(1L, dto);

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldSetPriorityToMedium() {
        var ticket = buildTicket();
        var dto = new TicketPriorityUpdateRequest();
        dto.setPriority(Priority.MEDIUM.name());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        var result = setTicketPriorityService.execute(1L, dto);

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldSetPriorityToLow() {
        var ticket = buildTicket();
        var dto = new TicketPriorityUpdateRequest();
        dto.setPriority(Priority.LOW.name());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        var result = setTicketPriorityService.execute(1L, dto);

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldThrowExceptionWhenPriorityIsNotMeasured() {
        var ticket = buildTicket();
        var dto = new TicketPriorityUpdateRequest();
        dto.setPriority(Priority.NOT_MEASURED.name());

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(TicketPriorityCantBeChanged.class, () ->
                setTicketPriorityService.execute(1L, dto));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPriorityValueIsInvalid() {
        var ticket = buildTicket();
        var dto = new TicketPriorityUpdateRequest();
        dto.setPriority("UNKNOWN_PRIORITY");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(TicketPriorityCantBeChanged.class, () ->
                setTicketPriorityService.execute(1L, dto));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        var dto = new TicketPriorityUpdateRequest();
        dto.setPriority(Priority.HIGH.name());

        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                setTicketPriorityService.execute(99L, dto));
    }
}

