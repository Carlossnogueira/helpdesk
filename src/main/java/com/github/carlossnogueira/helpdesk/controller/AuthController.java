package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.auth.LoginDto;
import com.github.carlossnogueira.helpdesk.business.dto.auth.TokenDto;
import com.github.carlossnogueira.helpdesk.business.user.AuthenticateUserService;
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

    @PostMapping("/auth")
    public ResponseEntity<TokenDto> authenticate(@Valid @RequestBody LoginDto loginDto) {
        var token = this.authenticateUserService.generateToken(loginDto);
        return ResponseEntity.ok(token);
    }

}
