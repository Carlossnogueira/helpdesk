package com.github.carlossnogueira.helpdesk.business.comment;

import com.github.carlossnogueira.helpdesk.business.dto.comment.CommentResponse;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.comment.UnauthorizedTicketCommentException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.ticket.TicketNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.CommentRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.TicketRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCommentsService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public List<CommentResponse> execute(long ticketId, UserDetail userDetail) {

        var ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);

        boolean isUser = userDetail.role().equals("USER");

        if (isUser && ticket.getUser().getId() != userDetail.id()) {
            throw new UnauthorizedTicketCommentException();
        }

        return commentRepository.findAllByTicketId(ticketId)
                .stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .createdAt(comment.getCreatedAt())
                        .authorName(comment.getAuthor().getName())
                        .ticketId(comment.getTicket().getId())
                        .build()
                )
                .toList();
    }

}

