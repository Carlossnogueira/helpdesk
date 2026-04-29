package com.github.carlossnogueira.helpdesk.business.dto.ticket;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TicketPriorityUpdateRequest {

    @NotNull(message = "Status is required")
    private String priority;
}
