package com.github.carlossnogueira.helpdesk.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column(nullable = false)
    private String Name;

    @Column(unique = true, nullable = false)
    private String Email;

    private String Password;

    private Role role = Role.USER;
}
