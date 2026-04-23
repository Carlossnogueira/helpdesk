package com.github.carlossnogueira.helpdesk.business.dto.ticket;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class TicketDto {

    @NotEmpty(message = "Title is required")
    @Length(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotEmpty(message = "Description is required")
    @Length(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

}
