package com.ilumusecase.jobs_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.repositories.interfaces.JobNodesRepository;
import com.ilumusecase.jobs_manager.resources.JobNode;

@RestController
public class JobsNodeController {

    @Autowired
    JobNodesRepository jobNodesRepository;

    Long counter = 0l;

    // @GetMapping("/projects/{project_id}/job_nodes/{job_node_name}")
    // public JobNode getJobNode(@PathVariable("project_id") Long projectId, @PathVariable("job_node_name")  String jobNodeName){
    //     return jobNodesRepository.findByProjectAndName(projectId, jobNodeName).get(); 
    // }

    // @PostMapping("/projects/{project_id}/job_nodes/{job_node_name}")
    // public JobNode createJobNode(@PathVariable("project_id") Long projectId, @PathVariable("job_node_name")  String jobNodeName){
    //     JobNode jobNode = new JobNode();
    //     jobNode.setId(counter++);
    //     jobNode.setName(jobNodeName);
    //     jobNode.setProjectId(projectId);
    //     return jobNodesRepository.save(jobNode);
    // }
}
