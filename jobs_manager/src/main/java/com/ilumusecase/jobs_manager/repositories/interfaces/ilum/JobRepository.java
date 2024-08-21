package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;


import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Validated
public interface JobRepository {
    
    public List<JobEntity> retrieveQueue(
        String jobNodeId,
        String queueType,
        String jobEntityName,
        String author,
        @Min(1) Integer pageSize,
        @Min(0) Integer pageNumber
    );
    public long retrieveQueueCount(
        String jobNodeId, String queueType, String jobEntityName, String author
    );

    public Optional<JobEntity> retrieveJobEntity(String id);
    public void deleteJob(String id);
    public JobEntity updateJobFull(@Valid JobEntity jobEntity);

}
