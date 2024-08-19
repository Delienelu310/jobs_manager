package com.ilumusecase.jobs_manager.exceptions.handling;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ilumusecase.jobs_manager.exceptions.security.NotAuthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;


@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        StringBuilder message = new StringBuilder();

        message.append("Validation Exception:");

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            message.append("\n" + errorMessage );
        });

        return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseBody
    public ResponseEntity<String> handleAuthorizationException(NotAuthorizedException ex){
        return new ResponseEntity<>("You are not authorized to do this action", HttpStatus.BAD_REQUEST);
    }

}
