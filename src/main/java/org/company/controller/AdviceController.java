package org.company.controller;

import org.company.exp.BadRequestException;
import org.company.exp.ItemNotFoundException;
import org.company.exp.NotPermissionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler({BadRequestException.class, ItemNotFoundException.class})
    public ResponseEntity<String> handler(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<String> handler(NotPermissionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> errorDetail = new HashMap<>();

            String[] codes = error.getCodes();
            String errorCode = (codes != null && codes.length > 0) ? codes[0] : "invalid_field";

            errorDetail.put("code", errorCode);
            errorDetail.put("message", error.getDefaultMessage());

            errors.put(error.getField(), errorDetail);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
