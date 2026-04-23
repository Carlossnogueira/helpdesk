package com.github.carlossnogueira.helpdesk.business.user;

import com.github.carlossnogueira.helpdesk.business.dto.auth.LoginDto;
import com.github.carlossnogueira.helpdesk.business.dto.auth.TokenDto;
import com.github.carlossnogueira.helpdesk.infrastructure.exception.user.EmailOrPasswordIncorrectException;
import com.github.carlossnogueira.helpdesk.infrastructure.repository.UserRepository;
import com.github.carlossnogueira.helpdesk.infrastructure.security.JwtProvider;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserService {

    @Autowired
    private UserRepository  userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    public TokenDto generateToken(LoginDto loginDto){
        var user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new EmailOrPasswordIncorrectException()
        );

        var passwordMatches = passwordEncoder.matches(loginDto.getPassword(), user.getPassword());

        if(!passwordMatches){
            throw new EmailOrPasswordIncorrectException();
        }

        var token =  jwtProvider.getJwtToken(new UserDetail(
                user.getId(),
                user.getName(),
                user.getRole().name()
        ));

        return new TokenDto(token);
    }

}
