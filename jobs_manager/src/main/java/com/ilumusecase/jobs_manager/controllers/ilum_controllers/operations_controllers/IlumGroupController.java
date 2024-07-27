package com.ilumusecase.jobs_manager.controllers.ilum_controllers.operations_controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroupConfiguraion;
import com.ilumusecase.jobs_manager.schedulers.JobEntityScheduler;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class IlumGroupController {


    @Autowired
    private RepositoryFactory repositoryFactory;

    @Autowired
    private Manager manager;

    @Autowired
    private JobEntityScheduler jobEntityScheduler;


    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/start")
    public void startJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestBody IlumGroupConfiguraion ilumGroupConfiguraion
    ){
        //step 0 : validation
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();

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
        ilumGroup.setIlumGroupConfiguraion(ilumGroupConfiguraion);     
        
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
        }catch(Exception e){
            throw new RuntimeException();
        }

    }


    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/stop")
    public void stopIlumGroup(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if(jobNode.getIlumGroup() == null){
            throw new RuntimeException();
        }
        IlumGroup ilumGroup = jobNode.getIlumGroup();
        
        //step 1: stop the lifecycle
        try{
            jobEntityScheduler.stopIlumGroupLifecycle(ilumGroup);
        }catch(Exception e){
            logger.info(e.getMessage());
            // throw new RuntimeException();
        }

        //step 2: stop current job
        manager.stopJob(ilumGroup.getCurrentJob());

        
        //step 3: delete ilum group in ilum-core
        manager.stopGroup(ilumGroup);
        manager.deleteGroup(ilumGroup);

        //step 4: delete ilum group metadata
        jobNode.setIlumGroup(null);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
        repositoryFactory.getIlumGroupRepository().deleteById(ilumGroup.getId());

        
    }
}
