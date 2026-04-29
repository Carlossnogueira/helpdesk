package com.github.carlossnogueira.helpdesk.business.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class CommentDto {

    @Length(min = 3, max = 500, message = "Text must be at most 500 characters and more than 3")
    @NotBlank(message = "Text is required")
    private String text;

}

