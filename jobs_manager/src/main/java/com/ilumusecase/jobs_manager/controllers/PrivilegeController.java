package com.ilumusecase.jobs_manager.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.resources.JobNodePrivilege;

@RestController
public class PrivilegeController {
    

    @PutMapping("/project/{project_id}/job_node/{job_node_id}/privilege/add/{user_id}/{privilege}")
    public void addPrivilegeToJobNode(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") JobNodePrivilege privilege
    ){

    }

    @PutMapping("/project/{project_id}/job_node/{job_node_id}/privilege/remove/{user_id}/{privilege}")
    public void removePrivilegeFromJobNode(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") JobNodePrivilege privilege
    ){

    }

    @PutMapping("/project/{project_id}/privilege/add/{user_id}/{privilege}")
    public void addPrivilegeToProject(
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") JobNodePrivilege privilege
    ){

    }


    @PutMapping("/project/{project_id}/privilege/remove/{user_id}/{privilege}")
    public void removePrivilegeFromProject(
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") JobNodePrivilege privilege
    ){

    }

}
