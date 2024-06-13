package com.ilumusecase.jobs_manager.files_validators;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.ilumusecase.jobs_manager.resources.JobNode;

public interface FilesValidator {
    
    public List<String> retrieveFileClasses(MultipartFile multipartFile);
    public boolean validate(MultipartFile file, JobNode jobNode, List<String> jobClasses);
}
