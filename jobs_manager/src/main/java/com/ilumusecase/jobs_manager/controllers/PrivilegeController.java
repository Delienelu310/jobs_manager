package com.ilumusecase.jobs_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.AppUser;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.PrivilegeList;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.resources.ProjectPrivilege;

@RestController
public class PrivilegeController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    

    @PutMapping("/project/{project_id}/job_node/{job_node_id}/privilege/add/{user_id}/{privilege}")
    public void addPrivilegeToJobNode(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") JobNodePrivilege privilege
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(userId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !project.getId().equals(jobNode.getProject().getId())) throw new RuntimeException();

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        jobNode.getPrivileges().putIfAbsent(appUser, new PrivilegeList<>());
        jobNode.getPrivileges().get(appUser).getList().add(privilege);

        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    }

    @PutMapping("/project/{project_id}/job_node/{job_node_id}/privilege/remove/{user_id}/{privilege}")
    public void removePrivilegeFromJobNode(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") JobNodePrivilege privilege
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(userId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !project.getId().equals(jobNode.getProject().getId())) throw new RuntimeException();

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        if( !jobNode.getPrivileges().containsKey(appUser)) throw new RuntimeException();

        jobNode.getPrivileges().get(appUser).getList().remove(privilege);

        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    }

    @PutMapping("/project/{project_id}/privilege/add/{user_id}/{privilege}")
    public void addPrivilegeToProject(
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") ProjectPrivilege privilege
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(userId);

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        project.getPrivileges().putIfAbsent(appUser, new PrivilegeList<>());
        project.getPrivileges().get(appUser).getList().add(privilege);

        repositoryFactory.getProjectRepository().updateProjectFull(project);
    }


    @PutMapping("/project/{project_id}/privilege/remove/{user_id}/{privilege}")
    public void removePrivilegeFromProject(
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") ProjectPrivilege privilege
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(userId);

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        project.getPrivileges().get(appUser).getList().remove(privilege);

        repositoryFactory.getProjectRepository().updateProjectFull(project);
    }

}
