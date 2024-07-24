package com.ilumusecase.jobs_manager.repositories.interfaces;


import java.util.List;

import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

public interface JobRepository {
    

    public List<JobEntity> retrieveQueue(
        String jobNodeId,
        String queueType,
        String jobEntityName,
        String author,
        Integer pageSize,
        Integer pageNumber
    );

    public JobEntity retrieveJobEntity(String id);
    public void deleteJob(String id);
    public JobEntity updateJobFull(JobEntity jobEntity);

}
