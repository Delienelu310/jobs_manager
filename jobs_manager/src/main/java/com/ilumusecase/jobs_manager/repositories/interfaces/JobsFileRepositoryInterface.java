package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;

import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;

public interface JobsFileRepositoryInterface {
    
    public JobsFile retrieveJobsFileById(String id);
    public List<JobsFile> retrieveJobsFilesByAuthorUsername(String username);
    public List<JobsFile> retrieveJobsFilesByJobNodeId(String id);
    public JobsFile updateJobsFileFull(JobsFile jobsFile);
    public void deleteJobsFileById(String id);

}
