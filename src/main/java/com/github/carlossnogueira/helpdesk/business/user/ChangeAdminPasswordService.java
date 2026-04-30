package com.github.carlossnogueira.helpdesk.business.user;

import com.github.carlossnogueira.helpdesk.business.dto.user.ChangePasswordDto;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.CurrentPasswordIncorrectException;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.UserNotFoundException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangeAdminPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void execute(ChangePasswordDto dto, UserDetail userDetail) {
        var user = userRepository.findById(userDetail.id())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new CurrentPasswordIncorrectException();
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

}

