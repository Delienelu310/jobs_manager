package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;

import java.util.List;
import java.util.Optional;

import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;

public interface JobsFileRepositoryInterface {


    public List<JobsFile> retrieveJobsFilesOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String className, 
        String publisher, 
        Integer pageSize, 
        Integer pageNumber
    );

    public long countJobsFilesOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String className, 
        String publisher
    );
    
    public Optional<JobsFile> retrieveJobsFileById(String id);
    public List<JobsFile> retrieveJobsFilesByAuthorUsername(String username);
    public List<JobsFile> retrieveJobsFilesByJobNodeId(String id);
    public JobsFile updateJobsFileFull(JobsFile jobsFile);
    public void deleteJobsFileById(String id);

}
