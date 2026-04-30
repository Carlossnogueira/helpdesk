package com.github.carlossnogueira.helpdesk.Comment;

import com.github.carlossnogueira.helpdesk.business.comment.ListCommentsService;
import com.github.carlossnogueira.helpdesk.business.dto.comment.CommentResponse;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Comment;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Role;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.comment.UnauthorizedTicketCommentException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.CommentRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListCommentsServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private ListCommentsService listCommentsService;

    private User buildUser(long id, String name) {
        return User.builder().id(id).name(name).email(name.toLowerCase() + "@example.com").build();
    }

    private Ticket buildTicket(long id, User owner) {
        return Ticket.builder()
                .id(id)
                .title("Some issue")
                .description("Detailed description of the issue")
                .status(Status.OPEN)
                .priority(Priority.NOT_MEASURED)
                .createdAt(LocalDateTime.now())
                .user(owner)
                .build();
    }

    private Comment buildComment(long id, String text, User author, Ticket ticket) {
        return Comment.builder()
                .id(id)
                .text(text)
                .createdAt(LocalDateTime.now())
                .author(author)
                .ticket(ticket)
                .build();
    }

    @Test
    void shouldListCommentsForTicketOwner() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner);
        var support = buildUser(50L, "Support");
        var userDetail = new UserDetail(1L, "Alice", Role.USER.name());

        var comments = List.of(
                buildComment(1L, "I opened this ticket", owner, ticket),
                buildComment(2L, "We are reviewing it", support, ticket)
        );

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(commentRepository.findAllByTicketId(10L)).thenReturn(comments);

        List<CommentResponse> result = listCommentsService.execute(10L, userDetail);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("I opened this ticket", result.get(0).getText());
        assertEquals("Alice", result.get(0).getAuthorName());
        assertEquals("We are reviewing it", result.get(1).getText());
        assertEquals("Support", result.get(1).getAuthorName());
    }

    @Test
    void shouldListCommentsForAdmin() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner);
        var admin = new UserDetail(99L, "Admin", Role.ADMIN.name());

        var comments = List.of(buildComment(1L, "Initial comment", owner, ticket));

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(commentRepository.findAllByTicketId(10L)).thenReturn(comments);

        List<CommentResponse> result = listCommentsService.execute(10L, admin);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getTicketId());
    }

    @Test
    void shouldListCommentsForSupport() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner);
        var support = new UserDetail(50L, "Support", Role.SUPPORT.name());

        var comments = List.of(buildComment(1L, "Support comment", buildUser(50L, "Support"), ticket));

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(commentRepository.findAllByTicketId(10L)).thenReturn(comments);

        List<CommentResponse> result = listCommentsService.execute(10L, support);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowExceptionWhenUserAccessesOtherUserTicketComments() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner);
        var otherUser = new UserDetail(2L, "Bob", Role.USER.name());

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        assertThrows(UnauthorizedTicketCommentException.class, () ->
                listCommentsService.execute(10L, otherUser));

        verify(commentRepository, never()).findAllByTicketId(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        var userDetail = new UserDetail(1L, "Alice", Role.USER.name());

        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                listCommentsService.execute(99L, userDetail));
    }
}

