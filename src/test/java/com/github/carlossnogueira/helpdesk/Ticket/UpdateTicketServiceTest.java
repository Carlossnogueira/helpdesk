package com.github.carlossnogueira.helpdesk.Ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketUpdateDto;
import com.github.carlossnogueira.helpdesk.business.ticket.UpdateTicketService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketAlreadyClosed;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
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
public class UpdateTicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private UpdateTicketService updateTicketService;

    private User buildUser() {
        return User.builder().id(1L).name("Alice").email("alice@example.com").build();
    }

    private Ticket buildTicket(Status status) {
        return Ticket.builder()
                .id(1L)
                .title("Old Title")
                .description("Old description text here")
                .status(status)
                .priority(Priority.NOT_MEASURED)
                .createdAt(LocalDateTime.now())
                .user(buildUser())
                .build();
    }

    @Test
    void shouldUpdateTicketTitleAndDescription() {
        var ticket = buildTicket(Status.OPEN);

        var dto = new TicketUpdateDto();
        dto.setTitle("New Title");
        dto.setDescription("Updated description for this ticket");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        var result = updateTicketService.execute(1L, dto);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("Updated description for this ticket", result.getDescription());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldKeepOldValueWhenFieldIsEmpty() {
        var ticket = buildTicket(Status.OPEN);

        var dto = new TicketUpdateDto();
        dto.setTitle("");
        dto.setDescription("");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        var result = updateTicketService.execute(1L, dto);

        assertNotNull(result);
        assertEquals("Old Title", result.getTitle());
        assertEquals("Old description text here", result.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenTicketIsClosed() {
        var ticket = buildTicket(Status.CLOSED);
        var dto = new TicketUpdateDto();
        dto.setTitle("Any title");
        dto.setDescription("Any description");

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(TicketAlreadyClosed.class, () ->
                updateTicketService.execute(1L, dto));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        var dto = new TicketUpdateDto();
        dto.setTitle("Title");
        dto.setDescription("Description");

        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                updateTicketService.execute(99L, dto));
    }
}

