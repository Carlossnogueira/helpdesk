package com.github.carlossnogueira.helpdesk.infrastructure.repository;

import com.github.carlossnogueira.helpdesk.infrastructure.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTicketId(long ticketId);
}

