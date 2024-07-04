package com.ilumusecase.jobs_manager.files_validators;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

public interface FilesValidator {
    
    public Set<String> retrieveFileClasses(MultipartFile multipartFile);
}
