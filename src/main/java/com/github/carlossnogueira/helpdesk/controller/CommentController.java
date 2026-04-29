package com.github.carlossnogueira.helpdesk.controller;

import com.github.carlossnogueira.helpdesk.business.comment.AddCommentService;
import com.github.carlossnogueira.helpdesk.business.comment.ListCommentsService;
import com.github.carlossnogueira.helpdesk.business.dto.comment.CommentDto;
import com.github.carlossnogueira.helpdesk.business.dto.comment.CommentResponse;
import com.github.carlossnogueira.helpdesk.documentation.DefaultErrorResponses;
import com.github.carlossnogueira.helpdesk.documentation.DeniedAccessResponses;
import com.github.carlossnogueira.helpdesk.documentation.ResourceNotFoundResponses;
import com.github.carlossnogueira.helpdesk.infrastructure.security.core.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private AddCommentService addCommentService;

    @Autowired
    private ListCommentsService listCommentsService;

    @Operation(summary = "Add a comment to a ticket")
    @ApiResponse(responseCode = "200", description = "Returns the created comment")
    @DefaultErrorResponses
    @ResourceNotFoundResponses
    @PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'ADMIN')")
    @PostMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable long ticketId,
                                                      @Valid @RequestBody CommentDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        return ResponseEntity.ok(addCommentService.execute(ticketId, dto, userDetail));
    }

    @Operation(summary = "List all comments of a ticket")
    @ApiResponse(responseCode = "200", description = "Returns the list of comments for the ticket")
    @DeniedAccessResponses
    @ResourceNotFoundResponses
    @PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'ADMIN')")
    @GetMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<List<CommentResponse>> listComments(@PathVariable long ticketId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        return ResponseEntity.ok(listCommentsService.execute(ticketId, userDetail));
    }

}

