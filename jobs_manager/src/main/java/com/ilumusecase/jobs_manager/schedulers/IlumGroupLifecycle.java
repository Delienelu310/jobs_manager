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

import com.fasterxml.jackson.databind.JsonNode;
import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobResult;

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
        JsonNode jobInfo = manager.getJobInfo(ilumGroup, ilumGroup.getCurrentJob());
        boolean isError = !jobInfo.get("error").isNull();
        boolean isFinished = !isError && !jobInfo.get("result").isNull();
        
        
        //if job state is not finished/error, then you should skip tick or stop the job, if it was to long
        if(
            !isFinished &&
            !isError
        ){
            long timePassed = Duration.between(LocalDateTime.now(), ilumGroup.getCurrentStartTime()).getSeconds();
            if(timePassed >= ilumGroup.getIlumGroupConfiguraion().getMaxJobDuration()){
                manager.stopJob(ilumGroup.getCurrentJob());

                jobInfo = manager.getJobInfo(ilumGroup, ilumGroup.getCurrentJob());
                isError = !jobInfo.get("error").isNull();
                isFinished = !isError && !jobInfo.get("result").isNull();
            }else{
                return;
            }
        }


        //create job result
        JobResult jobResult = new JobResult();
        jobResult.setIlumGroupId(ilumGroupId);
        jobResult.setIlumId(jobInfo.get("jobInstanceId").asText());
        //todo: create job result fully


        //get count of testing jobs and regular jobs:
        long testingJobsCount = repositoryFactory.getJobRepository().retrieveQueueCount(
            ilumGroup.getJobNode().getId(), "testingJobs", "", "");
        long jobsQueueCount = repositoryFactory.getJobRepository().retrieveQueueCount(
            ilumGroup.getJobNode().getId(), "jobsQueue", "", "");
        
            
        if(
            ilumGroup.getMod().equals("TEST") &&
            ilumGroup.getCurrentTestingIndex() < testingJobsCount - 1
        ){
            ilumGroup.setCurrentTestingIndex(ilumGroup.getCurrentTestingIndex() + 1);

            JobEntity newCurrentJob = repositoryFactory.getJobRepository().retrieveQueue(
                ilumGroup.getJobNode().getId(), 
                "testingJobs", 
                "", 
                "", 
                1, 
                ilumGroup.getCurrentIndex()
            ).get(0);
            ilumGroup.setCurrentJob(newCurrentJob);

            ilumGroup.setCurrentStartTime(LocalDateTime.now());
            repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        }else if(
            ilumGroup.getMod().equals("NORMAL") && 
            isError &&
            ilumGroup.getCurrentIndex() < jobsQueueCount - 1
            ||
            ilumGroup.getMod().equals("TEST") &&
            ilumGroup.getCurrentTestingIndex() >= testingJobsCount - 1 &&
            ilumGroup.getCurrentIndex() < jobsQueueCount - 1

        ){
            ilumGroup.setCurrentIndex(ilumGroup.getCurrentIndex() + 1);

            JobEntity newCurrentJob = repositoryFactory.getJobRepository().retrieveQueue(
                ilumGroup.getJobNode().getId(), 
                "jobsQueue", 
                "", 
                "", 
                1, 
                ilumGroup.getCurrentIndex()
            ).get(0);
            ilumGroup.setCurrentJob(newCurrentJob);

            ilumGroup.setMod("NORMAL");
            ilumGroup.setCurrentStartTime(LocalDateTime.now());
            repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        }else if(
            ilumGroup.getMod().equals("NORMAL") && 
            isError &&
            ilumGroup.getCurrentIndex() >= jobsQueueCount - 1
            ||
            ilumGroup.getCurrentTestingIndex() >= testingJobsCount - 1 &&
            ilumGroup.getCurrentIndex() >=jobsQueueCount - 1
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
            isFinished &&
                ilumGroup.getMod().equals("NORMAL")
        ){
            ilumGroup.setMod("TEST");
            ilumGroup.setCurrentTestingIndex(0);

            JobEntity newCurrentJob = repositoryFactory.getJobRepository().retrieveQueue(
                ilumGroup.getJobNode().getId(), 
                "testingJobs", 
                "", 
                "", 
                1, 
                0
            ).get(0);
            ilumGroup.setCurrentJob(newCurrentJob);


            ilumGroup.setCurrentStartTime(LocalDateTime.now());
            repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);
        }else{
            throw new RuntimeException("Unexpected behaviod: " +
                ilumGroup.getMod() + ", " +
                (isError ? "Error" : (isFinished ? "Finished" : "Strange state"))
            );
        }

        repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        Map<String, String> config = new HashMap<>();
        config.put("projectId", ilumGroup.getProject().getId());
        config.put("jobNodeId", ilumGroup.getJobNode().getId());
        config.put("mod", ilumGroup.getMod());
        config.put("prefix", "http://jobs-manager:8080");
        config.put("token", "Basic YWRtaW46YWRtaW4=");
        
        String ilumId = manager.submitJob(ilumGroup, ilumGroup.getCurrentJob(), config);
        ilumGroup.getCurrentJob().setIlumId(ilumId);
        repositoryFactory.getJobRepository().updateJobFull(ilumGroup.getCurrentJob());


        
    }
    
}
