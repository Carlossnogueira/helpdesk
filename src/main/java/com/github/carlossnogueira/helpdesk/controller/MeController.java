package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.MeDto;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class MeController {

    @GetMapping("/me")
    public ResponseEntity<MeDto> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) auth.getPrincipal();

        var me  = new MeDto(userDetail.id(), userDetail.name(), userDetail.role()) ;

        return ResponseEntity.ok(me);
    }

}
