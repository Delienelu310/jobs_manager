package com.ilumusecase.jobs_manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.JobNodeDetails;


@RestController
public class JobsNodeController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;

    @GetMapping("/job_nodes")
    public MappingJacksonValue retrieveAllNodes(){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNodeList(
            repositoryFactory.getJobNodesRepository().retrieveAll()
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}}")
    public MappingJacksonValue retrieveById(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode( 
            repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes")
    public MappingJacksonValue retrieveJobNodesByProjectId(@PathVariable("project_id") String projectId){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNodeList(
            repositoryFactory.getJobNodesRepository().retrieveByProjectId(projectId)
        );
    }

    @PostMapping("/projects/{project_id}/job_nodes")
    public MappingJacksonValue createJobNode(@PathVariable("project_id") String projectId, @RequestBody JobNodeDetails jobNodeDetails){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode(
            repositoryFactory.getJobNodesRepository().createJobNode(
                repositoryFactory.getProjectRepository().retrieveProjectById(projectId), 
                jobNodeDetails
            )
        );
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}")
    public MappingJacksonValue updateJobNodeDetails(@PathVariable("job_node_id") String jobNodeId, @RequestBody JobNodeDetails jobNodeDetails){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode(
            repositoryFactory.getJobNodesRepository().updateJobNode(jobNodeId, jobNodeDetails)
        );
    }

    
   
}
