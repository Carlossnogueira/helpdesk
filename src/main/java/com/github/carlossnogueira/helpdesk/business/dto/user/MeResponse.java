package com.github.carlossnogueira.helpdesk.business.dto.user;

public record MeResponse(
        long id,
        String name,
        String role
) {
}
