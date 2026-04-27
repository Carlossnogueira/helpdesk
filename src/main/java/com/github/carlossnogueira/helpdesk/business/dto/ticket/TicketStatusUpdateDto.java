package com.github.carlossnogueira.helpdesk.business.dto.ticket;

import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketStatusUpdateDto {

    @NotNull(message = "Status is required")
    private Status status;

}

