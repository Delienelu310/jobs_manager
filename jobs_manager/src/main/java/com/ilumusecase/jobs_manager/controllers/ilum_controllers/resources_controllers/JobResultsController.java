package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.ilum.JobResult;

import jakarta.validation.constraints.Min;

@RestController
public class JobResultsController {
    
    @Autowired
    private Manager manager;
    @Autowired
    private RepositoryFactory repositoryFactory;

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results")
    public List<List<JobResult>> retrieveJobResults(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,

        @RequestParam(name = "query", defaultValue = "", required = false) String targetName,
        @RequestParam(name = "sorted", defaultValue = "true", required = false) boolean sorted,
        @RequestParam(name = "tester", defaultValue = "", required = false) String testerId,

        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber     
    ){
        return null;
    }

    @GetMapping("/projects/projects/{project_id}/job_nodes/{job_node_id}/job_results/job_errors")
    public List<JobResult> retrieveJobErrors(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,

        @RequestParam(name = "query", defaultValue = "", required = false) String targetName,

        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber  
    ){
        return null;
    }

    @GetMapping("/projects/projects/{project_id}/job_nodes/{job_node_id}/job_results/tester_errors")
    public List<JobResult> retrieveTesterErrors(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,

        @RequestParam(name = "query", defaultValue = "", required = false) String targetName,
        @RequestParam(name = "tester", defaultValue = "", required = false) String testerId,

        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber  
    ){
        return null;
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/{job_result_id}")
    public void retrieveJobResultById(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_result_id") String jobResultId
    ){

    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_entities/{job_entity_id}/job_results")
    public void retrieveLastJobEntityResult(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_entity_id") String jobEntityId
    ){

    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/{job_result_id}")
    public void deleteResult(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_result_id") String jobResultId
    
    ){
        //todo: check if job result is of job node, and if job node is of project
        //todo: delete the job result also on the ilum core server

        repositoryFactory.getJobResultRepository().deleteJobResultById(jobResultId);
    }

}
