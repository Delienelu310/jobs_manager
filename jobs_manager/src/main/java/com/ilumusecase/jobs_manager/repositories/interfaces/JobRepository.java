package com.ilumusecase.jobs_manager.repositories.interfaces;


import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

public interface JobRepository {
    
    public JobEntity retrieveJobEntity(String id);
    public void deleteJob(String id);
    public JobEntity updateJobFull(JobEntity jobEntity);

}
