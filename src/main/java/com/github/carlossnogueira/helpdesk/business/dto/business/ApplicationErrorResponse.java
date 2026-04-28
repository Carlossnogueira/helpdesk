package com.github.carlossnogueira.helpdesk.business.dto.business;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ApplicationErrorResponse(
        @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        List<String> errors
) {
}
