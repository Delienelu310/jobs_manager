package com.ilumusecase.jobs_manager.exceptions.handling;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.exceptions.ResponseException;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;


@ControllerAdvice
public class GlobalExceptionHandler {


    private Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleInternalExceptions(Exception ex){

        logger.error("Internal exception:", ex.getMessage(), ex);
        
        return new ResponseEntity<>("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
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

    @ExceptionHandler(ResponseException.class)
    @ResponseBody
    public ResponseEntity<String> handleAuthorizationException(ResponseException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
