package com.ilumusecase.jobs_manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ilumusecase.jobs_manager.files_validators.FilesValidatorFactory;
import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.JobEntity;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class JobController {

    @Autowired
    private FilesValidatorFactory filesValidatorFactory;
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;


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

    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs")
    public MappingJacksonValue uploadJob(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam("files") List<MultipartFile> files
        //....

    ){
        //....
        return null;
    }


    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs/{job_id}")
    public void deleteJob(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_id") String jobId
    ){
        //...
    }



    
}
