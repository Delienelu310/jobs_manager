package com.ilumusecase.jobs_manager.schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Component
public class IlumGroupLifecycle implements Job{

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private Manager manager;
    @Autowired
    private Scheduler scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String ilumGroupId = jobDataMap.getString("ilumGroupId");

        
        IlumGroup ilumGroup = repositoryFactory.getIlumGroupRepository().retrieveById(ilumGroupId);


        //get current job state:
        String state = manager.getJobInfo(ilumGroup.getCurrentJob()).get("state").asText();
        //if job state is not finished/error, then you should skip tick or stop the job, if it was to long
        if(
            !ilumGroup.getCurrentJob().getState().equals("FINISHED") &&
            !ilumGroup.getCurrentJob().getState().equals("ERROR")
            
        ){
            long timePassed = Duration.between(LocalDateTime.now(), ilumGroup.getCurrentStartTime()).getSeconds();
            if(timePassed >= ilumGroup.getIlumGroupConfiguraion().getMaxJobDuration()){
                manager.stopJob(ilumGroup.getCurrentJob());
                state = manager.getJobInfo(ilumGroup.getCurrentJob()).get("state").asText();
            }else{
                return;
            }
        }
        //refresh the state in the db, if required
        if(!ilumGroup.getCurrentJob().getState().equals(state)){
            repositoryFactory.getJobRepository().updateJobFull(ilumGroup.getCurrentJob());
        }


        //do something depending on the state
        //now mod is expecte to be "FINISHED" or "ERROR"
        if(
            !ilumGroup.getCurrentJob().getState().equals("FINISHED") &&
            !ilumGroup.getCurrentJob().getState().equals("ERROR")
        ){
            return;
        }




        if(
            ilumGroup.getMod().equals("TEST") &&
            ilumGroup.getCurrentTestingIndex() < ilumGroup.getTestingJobs().size()
        ){
            ilumGroup.setCurrentTestingIndex(ilumGroup.getCurrentTestingIndex() + 1);
            ilumGroup.setCurrentJob(ilumGroup.getTestingJobs().get(ilumGroup.getCurrentIndex()));
            ilumGroup.setCurrentStartTime(LocalDateTime.now());
            repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        }else if(
            ilumGroup.getMod().equals("NORMAL") && 
            ilumGroup.getCurrentJob().getState().equals("ERROR") &&
            ilumGroup.getCurrentIndex() < ilumGroup.getJobs().size()
            ||
            ilumGroup.getMod().equals("TEST") &&
            ilumGroup.getCurrentTestingIndex() >= ilumGroup.getTestingJobs().size() &&
            ilumGroup.getCurrentIndex() < ilumGroup.getJobs().size()

        ){
            ilumGroup.setCurrentIndex(ilumGroup.getCurrentIndex() + 1);
            ilumGroup.setCurrentJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
            ilumGroup.setMod("NORMAL");
            ilumGroup.setCurrentStartTime(LocalDateTime.now());
            repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        }else if(
            ilumGroup.getMod().equals("NORMAL") && 
            ilumGroup.getCurrentJob().getState().equals("ERROR") &&
            ilumGroup.getCurrentIndex() >= ilumGroup.getJobs().size()
            ||
            ilumGroup.getCurrentTestingIndex() >= ilumGroup.getTestingJobs().size() &&
            ilumGroup.getCurrentIndex() >= ilumGroup.getJobs().size() 
        ){
            try {
                JobKey jobKey = new JobKey(ilumGroup.getId());
                scheduler.interrupt(jobKey);
                scheduler.deleteJob(jobKey);
            } catch (UnableToInterruptJobException e) {
                throw new RuntimeException(e);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }

            return;

        }else if(
            ilumGroup.getCurrentJob().getState().equals("FINISHED") &&
                ilumGroup.getMod().equals("NORMAL")
        ){
            ilumGroup.setMod("TEST");
            ilumGroup.setCurrentTestingIndex(0);
            ilumGroup.setCurrentJob(ilumGroup.getTestingJobs().get(0));
            ilumGroup.setCurrentStartTime(LocalDateTime.now());
            repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);
        }else{
            throw new RuntimeException("Unexpected behaviod: " +
                ilumGroup.getMod() + ", " +
                ilumGroup.getCurrentJob().getState()
            );
        }

        repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        Map<String, String> config = new HashMap<>();
        config.put("projectId", ilumGroup.getProject().getId());
        config.put("jobNodeId", ilumGroup.getJobNode().getId());
        config.put("mod", ilumGroup.getMod());
        config.put("prefix", "http://jobs-manager:8080");
        config.put("token", "Basic YWRtaW46YWRtaW4=");
        manager.submitJob(ilumGroup, ilumGroup.getCurrentJob(), config);

        
    }
    
}
