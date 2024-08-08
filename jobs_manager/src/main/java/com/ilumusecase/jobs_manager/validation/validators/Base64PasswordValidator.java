package com.ilumusecase.jobs_manager.validation.validators;

import java.util.Base64;

import com.ilumusecase.jobs_manager.validation.annotations.Base64Password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class Base64PasswordValidator implements ConstraintValidator<Base64Password, String>{

    private PasswordValidator passwordValidator = new PasswordValidator();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        try{
            String passwordDecoded = new String(Base64.getDecoder().decode(value));
            return passwordValidator.isValid(passwordDecoded, context);

        }catch(IllegalArgumentException e){

            return false;
            
        }
        
    }
    
}
