package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.business.GenericMessageResponse;
import com.github.carlossnogueira.helpdesk.business.dto.user.ChangePasswordDto;
import com.github.carlossnogueira.helpdesk.business.dto.user.UserDto;
import com.github.carlossnogueira.helpdesk.business.user.ChangeAdminPasswordService;
import com.github.carlossnogueira.helpdesk.business.user.RegisterSupportUserService;
import com.github.carlossnogueira.helpdesk.documentation.DefaultErrorResponses;
import com.github.carlossnogueira.helpdesk.documentation.DeniedAccessResponses;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RegisterSupportUserService registerSupportUserService;

    @Autowired
    private ChangeAdminPasswordService changeAdminPasswordService;

    @Operation(summary = "Register a new SUPPORT user (Admin only)")
    @ApiResponse(responseCode = "201", description = "Returns a success message with the location of the created support user")
    @DefaultErrorResponses
    @DeniedAccessResponses
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/support")
    public ResponseEntity<GenericMessageResponse> registerSupportUser(@Valid @RequestBody UserDto userDto) {
        var user = registerSupportUserService.execute(userDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(new GenericMessageResponse("Support user created successfully"));
    }

    @Operation(summary = "Change admin's own password")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @DefaultErrorResponses
    @DeniedAccessResponses
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/password")
    public ResponseEntity<GenericMessageResponse> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        changeAdminPasswordService.execute(dto, userDetail);
        return ResponseEntity.ok(new GenericMessageResponse("Password changed successfully"));
    }

}

