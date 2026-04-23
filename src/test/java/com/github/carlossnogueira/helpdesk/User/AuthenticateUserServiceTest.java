package com.github.carlossnogueira.helpdesk.User;

import com.github.carlossnogueira.helpdesk.business.dto.auth.LoginDto;
import com.github.carlossnogueira.helpdesk.business.dto.auth.TokenDto;
import com.github.carlossnogueira.helpdesk.business.user.AuthenticateUserService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Role;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.github.carlossnogueira.helpdesk.infrastructure.security.JwtProvider;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.EmailOrPasswordIncorrectException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthenticateUserService authenticateUserService;

    @Test
    void shouldGenerateJwtToken() {
        var loginDto = new LoginDto();
        loginDto.setEmail("johndoe@example.com");
        loginDto.setPassword("12345678910");

        var user = User.builder()
                .id(1L)
                .name("John Doe")
                .password("hashedPassword")
                .email(loginDto.getEmail())
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.getJwtToken(new UserDetail(user.getId(), user.getName(), user.getRole().name()))).thenReturn("jwt-token");

        TokenDto result = authenticateUserService.generateToken(loginDto);

        assertNotNull(result);
        assertNotNull(result.getToken());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        var loginDto = new LoginDto();
        loginDto.setEmail("notfound@example.com");
        loginDto.setPassword("any");

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(EmailOrPasswordIncorrectException.class, () -> {
            authenticateUserService.generateToken(loginDto);
        });
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        var loginDto = new LoginDto();
        loginDto.setEmail("johndoe@example.com");
        loginDto.setPassword("wrongpassword");

        var user = User.builder()
                .id(1L)
                .name("John Doe")
                .password("hashedPassword")
                .email(loginDto.getEmail())
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(EmailOrPasswordIncorrectException.class, () -> {
            authenticateUserService.generateToken(loginDto);
        });
    }
}
