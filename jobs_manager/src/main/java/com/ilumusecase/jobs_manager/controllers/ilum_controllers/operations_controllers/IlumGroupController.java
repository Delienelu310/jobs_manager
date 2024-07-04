package com.ilumusecase.jobs_manager.controllers.ilum_controllers.operations_controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
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




    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/start")
    public void startJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        if(jobNode.getJobsQueue().size() == 0){
            throw new RuntimeException("The queue is empty");
        }

        //1. create ilum group and bind it to job node

        IlumGroup ilumGroup = new IlumGroup();
        ilumGroup.setJobs(jobNode.getJobsQueue());
        ilumGroup.setTestingJobs(jobNode.getTestingJobs());
        ilumGroup.setCurrentIndex(0);
        ilumGroup.setJobNode(jobNode);

        String groupId = manager.createGroup(ilumGroup);
        ilumGroup.setIlumId(groupId);

        ilumGroup = repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        jobNode.setCurrentGroup(ilumGroup);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    
    
        //2. start jobs execution cycle
        //a. start execution of first job

        manager.submitJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()), new HashMap<>());
        try{
            jobEntityScheduler.startIlumGroupLifecycle(ilumGroup);
        }catch(Exception e){
            throw new RuntimeException();
        }
        
    }


    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/stop")
    public void stopJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if(jobNode.getCurrentGroup() == null){
            throw new RuntimeException();
        }
        IlumGroup ilumGroup = jobNode.getCurrentGroup();


        manager.stopJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));

        try{
            // jobEntityScheduler.deleteGroupStatusCheckScheduler(ilumGroup);
            // jobEntityScheduler.deleteJobEntityStop(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
        }catch(Exception e){
            throw new RuntimeException();
        }

        for(int i = 0; i < ilumGroup.getCurrentIndex(); i++){
            JobEntity jobEntity = ilumGroup.getJobs().get(i);
            jobNode.getJobsQueue().removeIf(jb -> jb.equals(jobEntity));
        }

        repositoryFactory.getIlumGroupRepository().deleteById(ilumGroup.getId());

        jobNode.setCurrentGroup(null);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    }
}
