package com.ilumusecase.jobs_manager.controllers.ilum_controllers.operations_controllers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.exceptions.GeneralResponseException;
import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroupConfiguraion;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroupDetails;
import com.ilumusecase.jobs_manager.schedulers.JobEntityScheduler;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@Validated
public class IlumGroupController {


    @Autowired
    private RepositoryFactory repositoryFactory;

    @Autowired
    private Manager manager;

    @Autowired
    private JobEntityScheduler jobEntityScheduler;


    record IlumGroupDTO(
        @Valid @NotNull IlumGroupConfiguraion ilumGroupConfiguration, 
        @Valid @NotNull IlumGroupDetails ilumGroupDetails
    ){

    }

    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/start")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER})
    public void startJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestBody @Valid @NotNull IlumGroupDTO ilumGroupDTO
    ){
        //step 0 : validation
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
     
        if(jobNode.getJobsQueue().size() == 0){
            throw new RuntimeException("The queue is empty");
        }
        if(jobNode.getTestingJobs().size() == 0){
            throw new RuntimeException("The testing jobs are absent");
        }

        // step 1: create ilum group on ilum-core:
        IlumGroup ilumGroup = new IlumGroup();
        ilumGroup.setJobNode(jobNode);
        ilumGroup.setProject(project);
        
        ilumGroup.setIlumGroupConfiguraion(ilumGroupDTO.ilumGroupConfiguration);  
        ilumGroupDTO.ilumGroupDetails.setStartTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000);
        ilumGroup.setIlumGroupDetails(ilumGroupDTO.ilumGroupDetails);   
        
        String groupId = manager.createGroup(ilumGroup);

        // step 2: create  ilum group 

        ilumGroup.setIlumId(groupId);
        repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        jobNode.setIlumGroup(ilumGroup);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        // step 3 submit first job

        //initial ilum group config:
        ilumGroup.setCurrentIndex(0);
        ilumGroup.setCurrentTestingIndex(0);
        ilumGroup.setCurrentStartTime(LocalDateTime.now());
        ilumGroup.setCurrentJob(ilumGroup.getJobNode().getJobsQueue().get(0));
        ilumGroup.setMod("NORMAL");
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

        // step 4 start lifecycle

        try{
            jobEntityScheduler.startIlumGroupLifecycle(ilumGroup);
        }catch(SchedulerException e){
            throw new RuntimeException("Shceduler exception when starting ilum group lifecycle");
        }

    }


    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/stop")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER})
    public void stopIlumGroup(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));;
        if(jobNode.getIlumGroup() == null){
            throw new GeneralResponseException("Job Node is not running and cannot be stopped");
        }
        IlumGroup ilumGroup = jobNode.getIlumGroup();
        
        
        try{
            //step 2: stop current job
            manager.stopJob(ilumGroup.getCurrentJob());

            //step 3: delete ilum group in ilum-core
            manager.stopGroup(ilumGroup);
            manager.deleteGroup(ilumGroup);
        }catch(Exception e){

        }
        
       
        //step 4: delete ilum group metadata
        jobNode.setIlumGroup(null);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
        repositoryFactory.getIlumGroupRepository().deleteById(ilumGroup.getId());
        
        //step 1: stop the lifecycle
        try{
            jobEntityScheduler.stopIlumGroupLifecycle(ilumGroup);
        }catch(SchedulerException e){
            throw new GeneralResponseException("Shceduler exceptoin when stopping ilum group lifecycle");
        }

        
    }
}
