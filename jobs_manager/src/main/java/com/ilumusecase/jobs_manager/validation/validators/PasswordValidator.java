package com.ilumusecase.jobs_manager.validation.validators;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.ilumusecase.jobs_manager.validation.annotations.Password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String>{


    private final Set<Character> specialCharacters = new HashSet<>();
    
    {
        Collections.addAll(this.specialCharacters, 
            '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
            ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'
        );
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        //password should be at least 8 chars
        //at least one char  of upper and one of lower case,
        //at least one digit
        //at least one char
        //at least one special sign
        //password cannot have any other characters, char letters, digits and special chars

        if(value == null) return false;
        if(value.length() < 8) return false;
        if(value.length() > 50) return false;

        boolean hasDigit = false;
        boolean hasUpperCaseChar = false;
        boolean hasLowerCaseChar = false;
        boolean hasSpecialSign = false;

        for(char c : value.toCharArray()){
            if(c >= '0' && c <= '9') hasDigit = true;
            else if(c >= 'A' && c <= 'Z') hasUpperCaseChar = true;
            else if(c >= 'a' && c <= 'z') hasLowerCaseChar = true;
            else if(this.specialCharacters.contains(c)) hasSpecialSign = true;
            else return false;
        }

        return hasDigit && hasUpperCaseChar && hasLowerCaseChar && hasSpecialSign;
    }
    
}
