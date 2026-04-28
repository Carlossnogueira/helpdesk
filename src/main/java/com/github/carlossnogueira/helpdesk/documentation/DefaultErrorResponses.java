package com.github.carlossnogueira.helpdesk.documentation;

import com.github.carlossnogueira.helpdesk.business.dto.business.ApplicationErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(schema = @Schema(implementation = ApplicationErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access denied")
})
public @interface DefaultErrorResponses  {
}
