package com.github.carlossnogueira.helpdesk.Ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDetailsResponse;
import com.github.carlossnogueira.helpdesk.business.ticket.GetTicketByIdService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Role;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
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
public class GetTicketByIdServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private GetTicketByIdService getTicketByIdService;

    private Ticket buildTicket(long ticketId, User owner) {
        return Ticket.builder()
                .id(ticketId)
                .title("Monitor issue")
                .description("Monitor flickering constantly")
                .status(Status.OPEN)
                .priority(Priority.NOT_MEASURED)
                .createdAt(LocalDateTime.now())
                .user(owner)
                .build();
    }

    @Test
    void shouldReturnTicketWhenOwnerUserRequests() {
        var owner = User.builder().id(1L).name("Alice").email("alice@example.com").build();
        var ticket = buildTicket(5L, owner);
        var userDetail = new UserDetail(1L, "Alice", Role.USER.name());

        when(ticketRepository.findById(5L)).thenReturn(Optional.of(ticket));

        TicketDetailsResponse result = getTicketByIdService.execute(5L, userDetail);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Alice", result.getCalledBy());
    }

    @Test
    void shouldReturnTicketWhenAdminRequests() {
        var owner = User.builder().id(2L).name("Bob").email("bob@example.com").build();
        var ticket = buildTicket(7L, owner);
        var adminDetail = new UserDetail(99L, "Admin", Role.ADMIN.name());

        when(ticketRepository.findById(7L)).thenReturn(Optional.of(ticket));

        TicketDetailsResponse result = getTicketByIdService.execute(7L, adminDetail);

        assertNotNull(result);
        assertEquals(7L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserAccessesOtherUserTicket() {
        var owner = User.builder().id(1L).name("Alice").email("alice@example.com").build();
        var ticket = buildTicket(5L, owner);
        var otherUser = new UserDetail(2L, "Carol", Role.USER.name());

        when(ticketRepository.findById(5L)).thenReturn(Optional.of(ticket));

        assertThrows(TicketNotFoundException.class, () ->
                getTicketByIdService.execute(5L, otherUser));
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        var userDetail = new UserDetail(1L, "Alice", Role.USER.name());

        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                getTicketByIdService.execute(99L, userDetail));
    }
}

