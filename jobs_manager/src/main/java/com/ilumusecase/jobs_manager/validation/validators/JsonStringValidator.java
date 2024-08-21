package com.ilumusecase.jobs_manager.validation.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilumusecase.jobs_manager.validation.annotations.JsonString;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JsonStringValidator implements ConstraintValidator<JsonString, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value.equals("")) return true;

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(value);
  
            return jsonNode.isObject();
        } catch (JsonProcessingException e) {
            return false;
        }
    }
    
}
