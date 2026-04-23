package com.github.carlossnogueira.helpdesk.infrastructure.exception;

import com.github.carlossnogueira.helpdesk.business.dto.business.ErrorOnValidationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorOnValidationResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(new ErrorOnValidationResponse(
                LocalDateTime.now(),
                400,
                errors
        ));
    }

}
