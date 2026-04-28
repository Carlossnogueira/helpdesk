package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.auth.LoginDto;
import com.github.carlossnogueira.helpdesk.business.dto.auth.TokenDto;
import com.github.carlossnogueira.helpdesk.business.user.AuthenticateUserService;
import com.github.carlossnogueira.helpdesk.documentation.InvalidDataResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class AuthController {

    @Autowired
    private AuthenticateUserService authenticateUserService;

    @Operation(summary = "Authenticate a user and generate a JWT token")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Returns a JWT token for the authenticated user")
    @InvalidDataResponses
    @PostMapping("/auth")
    public ResponseEntity<TokenDto> authenticate(@Valid @RequestBody LoginDto loginDto) {
        var token = this.authenticateUserService.generateToken(loginDto);
        return ResponseEntity.ok(token);
    }

}
