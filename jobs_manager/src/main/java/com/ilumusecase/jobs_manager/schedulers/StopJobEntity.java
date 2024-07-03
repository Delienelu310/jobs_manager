package com.ilumusecase.jobs_manager.schedulers;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

@Component
public class StopJobEntity implements Job{

    @Autowired
    private Manager manager;
    @Autowired
    private RepositoryFactory repositoryFactory;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String jobEntityId = jobDataMap.getString("jobEntityId");
    
        JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobEntityId);

        manager.stopJob(jobEntity);

    }
    
}
