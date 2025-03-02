package com.renting.rentingwebsite;

import com.renting.rentingwebsite.DTO.ErrorResponseDTO;
import com.stripe.exception.StripeException;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorResponseHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(500, "Something went wrong: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(ConfigDataResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(ConfigDataResourceNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(404, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(IllegalArgumentException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(400, "Validation failed: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StripeException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    public ResponseEntity<ErrorResponseDTO> handleStripeException(StripeException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(400, "Stripe failed: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FAILED_DEPENDENCY);
    }
}
