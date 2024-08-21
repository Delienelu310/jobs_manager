package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.validation.annotations.Username;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Validated
public interface JobsFileRepositoryInterface {


    public List<JobsFile> retrieveJobsFilesOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String className, 
        String publisher, 
        @Min(1) Integer pageSize, 
        @Min(0) Integer pageNumber
    );

    public long countJobsFilesOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String className, 
        String publisher
    );
    
    public Optional<JobsFile> retrieveJobsFileById(String id);
    public List<JobsFile> retrieveJobsFilesByAuthorUsername(@Username String username);
    public List<JobsFile> retrieveJobsFilesByJobNodeId(String id);
    public JobsFile updateJobsFileFull(@Valid JobsFile jobsFile);
    public void deleteJobsFileById(String id);

}
