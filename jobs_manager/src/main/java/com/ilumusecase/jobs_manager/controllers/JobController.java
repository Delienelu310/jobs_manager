package com.ilumusecase.jobs_manager.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class JobController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;
    @Autowired
    private Manager manager;

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs/{job_id}")
    public MappingJacksonValue retrieveJobEntity(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_id") String jobId
    ){
        // check if jobnode if of project id and if job is of jobnode id
        
        
        return jsonMappersFactory.getJobEntityMapper().getFullJobEntity(
            repositoryFactory.getJobRepository().retrieveJobEntity(jobId)
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs")
    public MappingJacksonValue retrieveJobEntitiesOfJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        //check if jobnoe if of project id

        return jsonMappersFactory.getJobEntityMapper().getFullJobEntityList(
            repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId).getJobsQueue()
        );
    }

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);


    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_queue/remove/{job_id}")
    public void removeJobFromQueue(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_id") String jobId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobId);
        IlumGroup ilumGroup = jobNode.getCurrentGroup();

        //in ilum group
        //if the job is currently running, stop it
        if(jobEntity.equals(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()))){
            manager.stopJob(jobEntity);
        }

        //if the job is in upcomming queue
        for(int i = ilumGroup.getCurrentIndex() + 1; i < ilumGroup.getJobs().size(); i++){
            if(ilumGroup.getJobs().get(i).equals(jobEntity)){
                ilumGroup.getJobs().remove(i);
                i--;
            }
        }
        ilumGroup = repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        //inside of job node
        jobNode.getJobsQueue().removeIf(jb -> jb.equals(jobEntity));
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

    }


}
