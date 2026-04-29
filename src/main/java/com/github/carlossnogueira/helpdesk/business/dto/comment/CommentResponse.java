package com.github.carlossnogueira.helpdesk.business.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private String authorName;
    private Long ticketId;
}

