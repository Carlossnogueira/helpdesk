package com.github.carlossnogueira.helpdesk.User;

import com.github.carlossnogueira.helpdesk.business.dto.user.UserDto;
import com.github.carlossnogueira.helpdesk.business.user.RegisterUserService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.UserAlreadyExistsException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Test
    void shouldRegisterUser() {

        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("johndoe@example.com");
        userDto.setPassword("12345678910");

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("hashed_password");

        var hashedPassword = passwordEncoder.encode(userDto.getPassword());

        User userToSave = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(hashedPassword)
                .build();

        User savedUser = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userToSave.getPassword())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = registerUserService.execute(userDto);


        assertNotNull(result);
        assertEquals(userToSave.getName(), result.getName());
        assertEquals(userToSave.getEmail(), result.getEmail());
        assertEquals(userToSave.getPassword(), result.getPassword());

        verify(userRepository).findByEmail(userDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserWithSameEmailAlreadyExists() {

        var existingUser = User.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password("12345678910")
                .build();

        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("johndoe@example.com");
        userDto.setPassword("12345678910");


        assertThrows(UserAlreadyExistsException.class, () -> {
            registerUserService.execute(userDto);
        });

        verify(userRepository).findByEmail(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

}
