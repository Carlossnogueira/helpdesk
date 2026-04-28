package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.business.GenericMessageResponse;
import com.github.carlossnogueira.helpdesk.business.dto.user.UserDto;
import com.github.carlossnogueira.helpdesk.business.user.RegisterUserService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private RegisterUserService registerUserService;

    @SecurityRequirements
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Returns a success message with the location of the created user"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or user already exists")
    })
    @PostMapping("/users")
    public ResponseEntity<GenericMessageResponse> registerUser(@Valid @RequestBody UserDto userDto) {
        var user = this.registerUserService.execute(userDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(new GenericMessageResponse("User created successfully"));
    }

}
