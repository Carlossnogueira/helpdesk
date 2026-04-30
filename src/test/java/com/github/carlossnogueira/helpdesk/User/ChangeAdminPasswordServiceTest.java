package com.github.carlossnogueira.helpdesk.User;

import com.github.carlossnogueira.helpdesk.business.dto.user.ChangePasswordDto;
import com.github.carlossnogueira.helpdesk.business.user.ChangeAdminPasswordService;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.enums.Role;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.CurrentPasswordIncorrectException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.UserNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChangeAdminPasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ChangeAdminPasswordService changeAdminPasswordService;

    private User buildAdmin() {
        return User.builder()
                .id(1L)
                .name("Admin")
                .email("admin@helpdesk.com")
                .password("hashed_old_password")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        var admin = buildAdmin();
        var userDetail = new UserDetail(1L, "Admin", Role.ADMIN.name());

        var dto = new ChangePasswordDto();
        dto.setCurrentPassword("admin@123");
        dto.setNewPassword("newPassword1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("admin@123", "hashed_old_password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword1")).thenReturn("hashed_new_password");
        when(userRepository.save(any(User.class))).thenReturn(admin);

        assertDoesNotThrow(() -> changeAdminPasswordService.execute(dto, userDetail));

        assertEquals("hashed_new_password", admin.getPassword());
        verify(userRepository).save(admin);
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        var admin = buildAdmin();
        var userDetail = new UserDetail(1L, "Admin", Role.ADMIN.name());

        var dto = new ChangePasswordDto();
        dto.setCurrentPassword("wrongPassword");
        dto.setNewPassword("newPassword1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongPassword", "hashed_old_password")).thenReturn(false);

        assertThrows(CurrentPasswordIncorrectException.class, () ->
                changeAdminPasswordService.execute(dto, userDetail));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        var userDetail = new UserDetail(99L, "Ghost", Role.ADMIN.name());

        var dto = new ChangePasswordDto();
        dto.setCurrentPassword("any");
        dto.setNewPassword("newPassword1");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                changeAdminPasswordService.execute(dto, userDetail));

        verify(userRepository, never()).save(any());
    }
}

