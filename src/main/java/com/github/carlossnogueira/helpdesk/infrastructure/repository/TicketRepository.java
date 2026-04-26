package com.github.carlossnogueira.helpdesk.infrastructure.repository;

import com.github.carlossnogueira.helpdesk.infrastructure.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTitle(String name);
    Optional<Ticket> findById(long id);
}
