package com.github.carlossnogueira.helpdesk.Ticket;

import com.github.carlossnogueira.helpdesk.business.ticket.ListTicketsService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Role;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListTicketsServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private ListTicketsService listTicketsService;

    private User buildUser(long id, String name) {
        return User.builder().id(id).name(name).email(name.toLowerCase() + "@example.com").build();
    }

    private Ticket buildTicket(long id, User owner) {
        return Ticket.builder()
                .id(id)
                .title("Issue #" + id)
                .description("Description for issue " + id)
                .status(Status.OPEN)
                .priority(Priority.NOT_MEASURED)
                .createdAt(LocalDateTime.now())
                .user(owner)
                .build();
    }

    @Test
    void shouldReturnAllTicketsForAdmin() {
        var admin = new UserDetail(99L, "Admin", Role.ADMIN.name());
        var user1 = buildUser(1L, "Alice");
        var user2 = buildUser(2L, "Bob");
        var tickets = List.of(buildTicket(1L, user1), buildTicket(2L, user2));
        var page = new PageImpl<>(tickets, PageRequest.of(0, 10), 2);

        when(ticketRepository.findAll(any(PageRequest.class))).thenReturn(page);

        var result = listTicketsService.execute(0, admin);

        assertNotNull(result);
        assertEquals(2, result.getTickets().size());
        verify(ticketRepository).findAll(any(PageRequest.class));
        verify(ticketRepository, never()).findAllByUserId(anyLong(), any());
    }

    @Test
    void shouldReturnAllTicketsForSupport() {
        var support = new UserDetail(50L, "Support", Role.SUPPORT.name());
        var user1 = buildUser(1L, "Alice");
        var tickets = List.of(buildTicket(1L, user1));
        var page = new PageImpl<>(tickets, PageRequest.of(0, 10), 1);

        when(ticketRepository.findAll(any(PageRequest.class))).thenReturn(page);

        var result = listTicketsService.execute(0, support);

        assertNotNull(result);
        assertEquals(1, result.getTickets().size());
        verify(ticketRepository).findAll(any(PageRequest.class));
        verify(ticketRepository, never()).findAllByUserId(anyLong(), any());
    }

    @Test
    void shouldReturnOnlyOwnTicketsForUser() {
        var user = new UserDetail(1L, "Alice", Role.USER.name());
        var owner = buildUser(1L, "Alice");
        var tickets = List.of(buildTicket(3L, owner));
        var page = new PageImpl<>(tickets, PageRequest.of(0, 10), 1);

        when(ticketRepository.findAllByUserId(eq(1L), any(PageRequest.class))).thenReturn(page);

        var result = listTicketsService.execute(0, user);

        assertNotNull(result);
        assertEquals(1, result.getTickets().size());
        assertEquals("Alice", result.getTickets().get(0).getCalledBy());
        verify(ticketRepository).findAllByUserId(eq(1L), any(PageRequest.class));
        verify(ticketRepository, never()).findAll(any(PageRequest.class));
    }
}


