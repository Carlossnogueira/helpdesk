package com.github.carlossnogueira.helpdesk.business.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class ChangePasswordDto {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Length(min = 8, max = 20, message = "New password must be between 8 and 20 characters")
    private String newPassword;

}

