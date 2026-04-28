package com.github.carlossnogueira.helpdesk.infrastructure.entity;

import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Priority;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tickets")
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @Builder.Default
    @Column(nullable = false)
    private Priority priority = Priority.NOT_MEASURED;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
