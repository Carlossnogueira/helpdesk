package com.github.carlossnogueira.helpdesk.business.dto.ticket;

import java.time.LocalDateTime;

public record TicketDetailsDto(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        String calledBy,
        String calledByEmail
) { }
