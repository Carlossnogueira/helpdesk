package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.business.GenericMessageDto;
import com.github.carlossnogueira.helpdesk.business.dto.user.UserDto;
import com.github.carlossnogueira.helpdesk.business.user.RegisterUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private RegisterUserService registerUserService;

    @PostMapping("/users")
    public ResponseEntity<GenericMessageDto> registerUser(@Valid @RequestBody UserDto userDto) {
        var user = this.registerUserService.execute(userDto);
        return ResponseEntity.ok(new GenericMessageDto("User registered successfully!"));
    }

}
