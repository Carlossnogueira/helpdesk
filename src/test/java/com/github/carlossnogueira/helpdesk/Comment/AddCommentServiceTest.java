package com.github.carlossnogueira.helpdesk.Comment;

import com.github.carlossnogueira.helpdesk.business.comment.AddCommentService;
import com.github.carlossnogueira.helpdesk.business.dto.comment.CommentDto;
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
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
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
public class AddCommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddCommentService addCommentService;

    private User buildUser(long id, String name) {
        return User.builder().id(id).name(name).email(name.toLowerCase() + "@example.com").build();
    }

    private Ticket buildTicket(long id, User owner, Status status) {
        return Ticket.builder()
                .id(id)
                .title("Some issue")
                .description("Detailed description of the issue")
                .status(status)
                .priority(Priority.NOT_MEASURED)
                .createdAt(LocalDateTime.now())
                .user(owner)
                .build();
    }

    private Comment buildSavedComment(long id, String text, User author, Ticket ticket) {
        return Comment.builder()
                .id(id)
                .text(text)
                .createdAt(LocalDateTime.now())
                .author(author)
                .ticket(ticket)
                .build();
    }

    @Test
    void shouldAddCommentAsUser() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner, Status.OPEN);
        var userDetail = new UserDetail(1L, "Alice", Role.USER.name());

        var dto = new CommentDto();
        dto.setText("I am still facing this issue");

        var saved = buildSavedComment(100L, dto.getText(), owner, ticket);

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.getReferenceById(1L)).thenReturn(owner);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentResponse result = addCommentService.execute(10L, dto, userDetail);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(dto.getText(), result.getText());
        assertEquals("Alice", result.getAuthorName());
        assertEquals(10L, result.getTicketId());

        // Ticket is OPEN but commenter is USER - status should NOT change
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldAddCommentAsAdminAndSetTicketToInProgress() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner, Status.OPEN);
        var admin = buildUser(99L, "Admin");
        var adminDetail = new UserDetail(99L, "Admin", Role.ADMIN.name());

        var dto = new CommentDto();
        dto.setText("We are looking into this");

        var saved = buildSavedComment(101L, dto.getText(), admin, ticket);

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(userRepository.getReferenceById(99L)).thenReturn(admin);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentResponse result = addCommentService.execute(10L, dto, adminDetail);

        assertNotNull(result);
        assertEquals(Status.IN_PROGRESS, ticket.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void shouldAddCommentAsSupportAndSetTicketToInProgress() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner, Status.OPEN);
        var support = buildUser(50L, "Support");
        var supportDetail = new UserDetail(50L, "Support", Role.SUPPORT.name());

        var dto = new CommentDto();
        dto.setText("Please provide more details");

        var saved = buildSavedComment(102L, dto.getText(), support, ticket);

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(userRepository.getReferenceById(50L)).thenReturn(support);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        addCommentService.execute(10L, dto, supportDetail);

        assertEquals(Status.IN_PROGRESS, ticket.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void shouldNotChangeStatusWhenTicketIsAlreadyInProgress() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner, Status.IN_PROGRESS);
        var admin = buildUser(99L, "Admin");
        var adminDetail = new UserDetail(99L, "Admin", Role.ADMIN.name());

        var dto = new CommentDto();
        dto.setText("Still checking the issue");

        var saved = buildSavedComment(103L, dto.getText(), admin, ticket);

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.getReferenceById(99L)).thenReturn(admin);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        addCommentService.execute(10L, dto, adminDetail);

        assertEquals(Status.IN_PROGRESS, ticket.getStatus());
        // Status was already IN_PROGRESS — save should NOT be called for ticket
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldNotChangeStatusWhenTicketIsClosed() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner, Status.CLOSED);
        var admin = buildUser(99L, "Admin");
        var adminDetail = new UserDetail(99L, "Admin", Role.ADMIN.name());

        var dto = new CommentDto();
        dto.setText("Closing note");

        var saved = buildSavedComment(104L, dto.getText(), admin, ticket);

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(userRepository.getReferenceById(99L)).thenReturn(admin);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        addCommentService.execute(10L, dto, adminDetail);

        assertEquals(Status.CLOSED, ticket.getStatus());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void shouldThrowExceptionWhenUserCommentsOnOtherUserTicket() {
        var owner = buildUser(1L, "Alice");
        var ticket = buildTicket(10L, owner, Status.OPEN);
        var otherUser = new UserDetail(2L, "Bob", Role.USER.name());

        var dto = new CommentDto();
        dto.setText("This is not my ticket");

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        assertThrows(UnauthorizedTicketCommentException.class, () ->
                addCommentService.execute(10L, dto, otherUser));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTicketNotFound() {
        var userDetail = new UserDetail(1L, "Alice", Role.USER.name());
        var dto = new CommentDto();
        dto.setText("Hello");

        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                addCommentService.execute(99L, dto, userDetail));
    }
}

