package com.ilumusecase.jobs_manager.files_validators;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;

public interface FilesValidator {
    
    public Set<String> retrieveFileClasses(MultipartFile multipartFile);
    public boolean validate(MultipartFile file, JobNode jobNode, List<String> jobClasses);
}
