package com.github.carlossnogueira.helpdesk.Ticket;

import com.github.carlossnogueira.helpdesk.business.dto.ticket.TicketDto;
import com.github.carlossnogueira.helpdesk.business.ticket.RegisterTicketService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterTicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterTicketService registerTicketService;

    @Test
    void shouldRegisterTicket() {
        var dto = new TicketDto();
        dto.setTitle("Printer broken");
        dto.setDescription("The office printer is not working since Monday");

        var user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        var savedTicket = Ticket.builder()
                .id(10L)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(Status.OPEN)
                .priority(Priority.NOT_MEASURED)
                .user(user)
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        var result = registerTicketService.registerTicket(dto, 1L);

        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(Status.OPEN, result.getStatus());
        assertEquals(Priority.NOT_MEASURED, result.getPriority());

        verify(userRepository).getReferenceById(1L);
        verify(ticketRepository).save(any(Ticket.class));
    }
}

