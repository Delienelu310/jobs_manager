package com.ilumusecase.jobs_manager.files_validators;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface FilesValidator {
    
    public Optional<String> validate(MultipartFile file, String expectedClass);
}
