package com.renting.rentingwebsite;

import com.renting.rentingwebsite.DTO.ErrorResponseDTO;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorResponseHandler.class);


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        log.warn(ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO(500, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        log.warn("{}: {}", ex.getClass(), ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(401, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponseDTO> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.warn("{}: {}", ex.getClass(), ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(ConfigDataResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(ConfigDataResourceNotFoundException ex) {
        log.warn("{}: {}", ex.getClass(), ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(404, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(IllegalArgumentException ex) {
        log.warn("{}: {}", ex.getClass(), ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(400, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StripeException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    public ResponseEntity<ErrorResponseDTO> handleStripeException(StripeException ex) {
        log.warn("{}: {}", ex.getClass(), ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(400, "Stripe failed: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FAILED_DEPENDENCY);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NoHandlerFoundException ex) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());
        response.put("path", ex.getRequestURL());
        return response;
    }
}
