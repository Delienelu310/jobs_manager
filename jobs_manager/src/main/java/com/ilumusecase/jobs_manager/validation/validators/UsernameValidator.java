package com.ilumusecase.jobs_manager.validation.validators;

import com.ilumusecase.jobs_manager.validation.annotations.Username;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<Username, String>{

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return false; 
        }
        return username.matches("^[a-zA-Z0-9]{3,20}$");
    }
    
}
