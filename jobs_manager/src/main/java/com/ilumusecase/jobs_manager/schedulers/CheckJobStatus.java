package com.ilumusecase.jobs_manager.schedulers;

import java.util.HashMap;

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
public class CheckJobStatus implements Job{

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

        JsonNode jobInfo = manager.getJobInfo(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));

        String state = jobInfo.get("state").asText();

        if(state.equals("WORKING")) return;

        //if the job is not working, but is already finished for any reason
        //1. change the current job in IlumGroup
        //2. stop the stopJobScheduled
        //3. execute next job or exit, if there`s no jobs left


        try {
            jobEntityScheduler.deleteJobEntityStop(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
        } catch (SchedulerException e) {
            throw new RuntimeException();
        }

        if(ilumGroup.getCurrentTestingIndex() == -1){
            ilumGroup.setCurrentTestingIndex(0);
            try {
                jobEntityScheduler.startJobTestStatusCheckScheduler(ilumGroup);
            } catch (SchedulerException e) {
                throw new RuntimeException();
            }
            repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);
        }
   
        if(ilumGroup.getCurrentTestingIndex() < ilumGroup.getTestingJobs().size() ) return;
        ilumGroup.setCurrentTestingIndex(-1);
        ilumGroup.setCurrentIndex(ilumGroup.getCurrentIndex() + 1);


        

        if(ilumGroup.getCurrentIndex() < ilumGroup.getJobs().size()){
            manager.submitJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()), new HashMap<>());
            try {
                jobEntityScheduler.scheduleJobEntityStop(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
            } catch (SchedulerException e) {
                throw new RuntimeException();
            }
        }else{
            try {
                jobEntityScheduler.deleteGroupStatusCheckScheduler(ilumGroup);
            } catch (SchedulerException e) { 
                throw new RuntimeException();
            }
        }
        repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);


    }
    
}
