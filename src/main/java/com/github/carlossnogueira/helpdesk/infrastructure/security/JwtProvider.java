package com.github.carlossnogueira.helpdesk.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtToken;

    @Value("${jwt.expiration}")
    private String expiresTime;

    public String getJwtToken(UserDetail userDetail) {
       return JWT.create()
               .withIssuer("HelpDesk")
               .withSubject(String.valueOf(userDetail.id()))
               .withClaim("name", userDetail.name())
               .withClaim("role", userDetail.role())
               .withExpiresAt(Instant.now().plusMillis(Long.parseLong(expiresTime)))
               .sign(Algorithm.HMAC256(jwtToken));
    }

    public UserDetail validateToken(String token) {
        token = token.replace("Bearer ", "");

        try {
            var decodedJWT = JWT.require(Algorithm.HMAC256(jwtToken))
                    .withIssuer("HelpDesk")
                    .build()
                    .verify(token);

            return new  UserDetail(Long.parseLong
                    (decodedJWT.getSubject()),
                    decodedJWT.getClaim("name").asString(),
                    decodedJWT.getClaim("role").asString()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
