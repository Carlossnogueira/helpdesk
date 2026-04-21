package com.github.carlossnogueira.helpdesk.infrastructure.exception;

import com.github.carlossnogueira.helpdesk.business.dto.ErrorOnValidationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HelpDeskExceptionBase.class)
    public ResponseEntity<ErrorOnValidationResponse> handleHelpDeskExceptionBase(HelpDeskExceptionBase ex) {
        var response = new ErrorOnValidationResponse(
                LocalDateTime.now(),
                ex.getHttpStatus().value(),
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

}
