package com.ilumusecase.jobs_manager.schedulers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobResult;
import com.ilumusecase.jobs_manager.resources.ilum.JobResultDetails;

@Component
public class IlumGroupLifecycle implements Job{

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private Manager manager;
    @Autowired
    private Scheduler scheduler;


    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    private Map<String, String> resultToMetrics(String resultStr ){

        Map<String, String> result = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(resultStr);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } 


        

        logger.info(jsonNode.toString());

        Iterator<Entry<String, JsonNode>> iterator = jsonNode.fields();
        
        
        while(iterator.hasNext()){
            Entry<String, JsonNode> field = iterator.next();
            

            //check validation later...

            result.put(field.getKey(), field.getValue().asText());
        }

        return result;
    }


    private void createJobResult(IlumGroup ilumGroup, JsonNode jobResultInfo){
        
        //create only if:
        //0. if the job result does not exist already
        //1. the job result info is of test 
        //2. if the job result if of regular job which failed

        if(repositoryFactory.getJobResultRepository().retrieveByIlumId(
            jobResultInfo.get("jobInstanceId").asText()
        ).isPresent()) return;

        if(ilumGroup.getMod().equals("NORMAL") && jobResultInfo.get("error").isNull()) return;

        JobResult jobResult = new JobResult();
        jobResult.setIlumGroupId(ilumGroup.getIlumId());
        jobResult.setIlumGroupDetails(ilumGroup.getIlumGroupDetails());
        jobResult.setIlumId(jobResultInfo.get("jobInstanceId").asText());

        jobResult.setJobNode(ilumGroup.getJobNode());
        jobResult.setProject(ilumGroup.getProject());
        
        
        if(ilumGroup.getMod().equals("NORMAL")){
            jobResult.setTester(null);
            jobResult.setTarget(ilumGroup.getCurrentJob().getJobScript());
            jobResult.setTargetConfiguration(ilumGroup.getCurrentJob().getConfiguration());

            jobResult.setStartTime(jobResultInfo.get("startTime").asLong());
            jobResult.setEndTime(jobResultInfo.get("endTime").asLong());

            JobResultDetails jobResultDetails = new JobResultDetails();
            jobResultDetails.setErrorMessage(jobResultInfo.get("error").get("message").asText() );
            jobResultDetails.setErrorStackTrace(jobResultInfo.get("error").get("stackTrace").asText());
            
            jobResultDetails.setResultStr(null);
            jobResultDetails.setMetrics(null);

            jobResult.setJobResultDetails(jobResultDetails);

        }else{
            jobResult.setTester(ilumGroup.getCurrentJob().getJobScript());

            JobEntity currentJob = repositoryFactory.getJobRepository().retrieveQueue(
                ilumGroup.getJobNode().getId(), 
                "jobsQueue", 
                "", 
                "", 
                1, 
                ilumGroup.getCurrentIndex()
            ).get(0);            
            jobResult.setTarget(currentJob.getJobScript());
            jobResult.setTargetConfiguration(currentJob.getConfiguration());

            JsonNode jobInfo = manager.getJobInfo(ilumGroup, currentJob);
            jobResult.setStartTime(jobInfo.get("startTime").asLong());
            jobResult.setEndTime(jobInfo.get("endTime").asLong());

            JobResultDetails jobResultDetails = new JobResultDetails();
            if(jobResultInfo.get("error").isNull()){
                
                jobResultDetails.setErrorMessage(null );
                jobResultDetails.setErrorStackTrace(null);
                
                jobResultDetails.setResultStr(jobInfo.get("result").asText());

                logger.info("Test result: " + jobResultInfo.get("result").asText());
                jobResultDetails.setMetrics(resultToMetrics(jobResultInfo.get("result").asText()));
            }else{
                jobResultDetails.setErrorMessage(jobResultInfo.get("error").get("message").asText() );
                jobResultDetails.setErrorStackTrace(jobResultInfo.get("error").get("stackTrace").asText());
                
                jobResultDetails.setResultStr(jobInfo.get("result").asText());
                jobResultDetails.setMetrics(null);
            }
            jobResult.setJobResultDetails(jobResultDetails);
        }   
        

        repositoryFactory.getJobResultRepository().updateJobResultFull(jobResult);
    }

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

        this.createJobResult(ilumGroup, jobInfo);

        //get count of testing jobs and regular jobs:
        long testingJobsCount = repositoryFactory.getJobRepository().retrieveQueueCount(
            ilumGroup.getJobNode().getId(), "testingJobs", "", "");
        long jobsQueueCount = repositoryFactory.getJobRepository().retrieveQueueCount(
            ilumGroup.getJobNode().getId(), "jobsQueue", "", "");
        
            
        if(
            ilumGroup.getMod().equals("TEST") &&
            ilumGroup.getCurrentTestingIndex() < testingJobsCount -1 
        ){
            ilumGroup.setCurrentTestingIndex(ilumGroup.getCurrentTestingIndex() + 1);

            JobEntity newCurrentJob = repositoryFactory.getJobRepository().retrieveQueue(
                ilumGroup.getJobNode().getId(), 
                "testingJobs", 
                "", 
                "", 
                1, 
                ilumGroup.getCurrentTestingIndex()
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
            ilumGroup.getCurrentTestingIndex() >= testingJobsCount - 1  &&
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
            ilumGroup.getMod().equals("TEST") &&
            (isFinished || isError) &&
            ilumGroup.getCurrentTestingIndex() >= testingJobsCount - 1 &&
            ilumGroup.getCurrentIndex() >=jobsQueueCount - 1
        ){
            
            
            
            //1. stop and delete ilum group from ilum-core
            manager.stopGroup(ilumGroup);
            manager.deleteGroup(ilumGroup);
            //2. delete ilum group from job node

            JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(ilumGroup.getJobNode().getId())
                .orElseThrow(RuntimeException::new);
            jobNode.setIlumGroup(null);
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

            repositoryFactory.getIlumGroupRepository().deleteById(ilumGroup.getId());


            // 3. stop the scheduled job
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
