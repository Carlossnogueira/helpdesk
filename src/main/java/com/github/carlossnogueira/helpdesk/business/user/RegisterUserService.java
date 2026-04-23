package com.github.carlossnogueira.helpdesk.business.user;

import com.github.carlossnogueira.helpdesk.business.dto.user.UserDto;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.UserAlreadyExistsException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User execute(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        var passwordHashed = passwordEncoder.encode(userDto.getPassword());

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordHashed)
                .build();

        return this.userRepository.save(user);
    }

}
