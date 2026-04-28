package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.dto.user.MeResponse;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class MeController {

    @Operation(summary = "Get the authenticated user's information")
    @ApiResponse(responseCode = "200", description = "Returns the authenticated user's information")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) auth.getPrincipal();

        var me  = new MeResponse(userDetail.id(), userDetail.name(), userDetail.role()) ;

        return ResponseEntity.ok(me);
    }

}
