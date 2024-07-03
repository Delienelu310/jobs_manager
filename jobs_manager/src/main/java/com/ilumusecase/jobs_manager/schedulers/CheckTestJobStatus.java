package com.ilumusecase.jobs_manager.schedulers;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Component
public class CheckTestJobStatus implements Job {
    @Autowired
    private Manager manager;
    @Autowired
    private JobEntityScheduler jobEntityScheduler;

    @Autowired
    private RepositoryFactory repositoryFactory;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String ilumGroupId = jobDataMap.getString("ilumGroupId");
        IlumGroup ilumGroup = repositoryFactory.getIlumGroupRepository().retrieveById(ilumGroupId);

        JsonNode jobInfo = manager.getJobInfo(ilumGroup.getTestingJobs().get(ilumGroup.getCurrentTestingIndex()));

        String state = jobInfo.get("state").asText();

        if(state.equals("WORKING")) return;

        //if the job is not working, but is already finished for any reason
        //1. change the current job in IlumGroup
        //2. stop the stopJobScheduled
        //3. execute next job or exit, if there`s no jobs left


        ilumGroup.setCurrentTestingIndex(ilumGroup.getCurrentTestingIndex() + 1);

        

        if(ilumGroup.getCurrentTestingIndex() < ilumGroup.getTestingJobs().size()){
            Map<String, String> config = new HashMap<>();
            manager.submitJob(ilumGroup.getTestingJobs().get(ilumGroup.getCurrentTestingIndex()), config);
            
        }else{
            try {
                jobEntityScheduler.deleteJobTestStatusCheckScheduler(ilumGroup);
            } catch (SchedulerException e) { 
                throw new RuntimeException();
            }

            
        }



    }
}
