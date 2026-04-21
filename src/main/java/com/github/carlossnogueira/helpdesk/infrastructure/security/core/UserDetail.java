package com.github.carlossnogueira.helpdesk.infrastructure.security.core;

public record UserDetail(
        Long id,
        String name,
        String role
) {
}
