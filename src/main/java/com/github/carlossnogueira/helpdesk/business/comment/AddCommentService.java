package com.github.carlossnogueira.helpdesk.business.comment;

import com.github.carlossnogueira.helpdesk.business.dto.comment.CommentDto;
import com.github.carlossnogueira.helpdesk.business.dto.comment.CommentResponse;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.Comment;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.comment.UnauthorizedTicketCommentException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketAlreadyClosed;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.CommentRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddCommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    public CommentResponse execute(long ticketId, CommentDto dto, UserDetail userDetail) {

        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);

        if(ticket.getStatus().equals(Status.CLOSED)) {
            throw new TicketAlreadyClosed();
        }

        boolean isUser = userDetail.role().equals("USER");
        boolean isAdminOrSupport = userDetail.role().equals("ADMIN") || userDetail.role().equals("SUPPORT");

        if (isUser && ticket.getUser().getId() != userDetail.id()) {
            throw new UnauthorizedTicketCommentException();
        }

        if (isAdminOrSupport && ticket.getStatus() == Status.OPEN) {
            ticket.setStatus(Status.IN_PROGRESS);
            ticketRepository.save(ticket);
        }

        var author = userRepository.getReferenceById(userDetail.id());

        var comment = Comment.builder()
                .text(dto.getText())
                .author(author)
                .ticket(ticket)
                .build();

        var saved = commentRepository.save(comment);

        return CommentResponse.builder()
                .id(saved.getId())
                .text(saved.getText())
                .createdAt(saved.getCreatedAt())
                .authorName(saved.getAuthor().getName())
                .ticketId(saved.getTicket().getId())
                .build();
    }

}

