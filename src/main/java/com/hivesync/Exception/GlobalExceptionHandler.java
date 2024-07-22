package com.hivesync.Exception;

import com.hivesync.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex) {
        ApiResponse response = new ApiResponse(false, "Authentication required. Please log in.");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Additional exception handlers can be added here
}
