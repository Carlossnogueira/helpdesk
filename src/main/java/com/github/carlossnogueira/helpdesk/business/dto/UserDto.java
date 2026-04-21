package com.github.carlossnogueira.helpdesk.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class UserDto {

    @NotEmpty(message = "Name is required")
    @Length(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Email
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    @Length(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;


}
