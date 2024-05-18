package com.ilumusecase.jobs_manager.files_validators;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class FilesValidatorFactory {

    private Map<String, FilesValidator> fileValidators = new HashMap<>();


    public FilesValidatorFactory(
        JarValidator jarValidator
    ){ 
        fileValidators.put("jar", jarValidator);
    }


    public Optional<FilesValidator> getValidator(String type){
        return Optional.ofNullable(fileValidators.get(type));
    }
}
