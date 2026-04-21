package com.github.carlossnogueira.helpdesk.business.user;

import com.github.carlossnogueira.helpdesk.business.dto.UserDto;
import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.UserAlreadyExistsException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService {

    @Autowired
    private UserRepository userRepository;

    public User execute(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();

        var result = this.userRepository.save(user);
        return result;
    }

}
