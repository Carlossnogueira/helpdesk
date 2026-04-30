package com.github.carlossnogueira.helpdesk.Ticket;

import com.github.carlossnogueira.helpdesk.business.ticket.DeleteTicketService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteTicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private DeleteTicketService deleteTicketService;

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
    void shouldDeleteTicket() {
        var ticket = buildTicket();

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        deleteTicketService.execute(1L);

        verify(ticketRepository).findById(1L);
        verify(ticketRepository).delete(ticket);
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                deleteTicketService.execute(99L));

        verify(ticketRepository, never()).delete(any());
    }
}

