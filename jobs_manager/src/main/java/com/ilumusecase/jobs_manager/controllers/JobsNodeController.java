package com.ilumusecase.jobs_manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;


@RestController
public class JobsNodeController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;

    @GetMapping("/job_nodes")
    public MappingJacksonValue retrieveAllNodes(){
        return null;
    }

    @GetMapping("/projects/{project_id/job_nodes/{job_node_id}}")
    public MappingJacksonValue retrieveById(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId){
        return null;   
    }
   
}
