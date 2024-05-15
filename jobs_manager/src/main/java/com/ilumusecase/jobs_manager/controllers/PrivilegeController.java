package com.ilumusecase.jobs_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.AppUser;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.resources.ProjectPrivilege;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;

@RestController
public class PrivilegeController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    

    @PutMapping("/project/{project_id}/job_nodes/{job_node_id}/privilege/add/{user_id}/{privilege}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void addPrivilegeToJobNode(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") JobNodePrivilege privilege
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !project.getId().equals(jobNode.getProject().getId())) throw new RuntimeException();

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        if(!jobNode.getPrivileges().containsKey(appUser.getUsername())){
            jobNode.getPrivileges().put(appUser.getUsername(), 
                repositoryFactory.getJobNodePrivilegeList().create()
            );
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
        }
        
        jobNode.getPrivileges().get(appUser.getUsername()).getList().add(privilege);
        repositoryFactory.getJobNodePrivilegeList().update(jobNode.getPrivileges().get(appUser.getUsername()));
    }

    @PutMapping("/project/{project_id}/job_nodes/{job_node_id}/privilege/remove/{user_id}/{privilege}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void removePrivilegeFromJobNode(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") JobNodePrivilege privilege
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !project.getId().equals(jobNode.getProject().getId())) throw new RuntimeException();

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        if( !jobNode.getPrivileges().containsKey(appUser.getUsername())) throw new RuntimeException();

        jobNode.getPrivileges().get(appUser.getUsername()).getList().remove(privilege);

        repositoryFactory.getJobNodePrivilegeList().update(jobNode.getPrivileges().get(appUser.getUsername()));
    }

    @PutMapping("/project/{project_id}/privilege/add/{user_id}/{privilege}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void addPrivilegeToProject(
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") ProjectPrivilege privilege
    ){

        if(privilege == ProjectPrivilege.MODERATOR){
            throw new RuntimeException();
        }

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);
        if(!project.getPrivileges().containsKey(appUser.getUsername())){
            project.getPrivileges().put(appUser.getUsername(), 
                repositoryFactory.getProjectPrivilegeList().create()
            );
            repositoryFactory.getProjectRepository().updateProjectFull(project);
        }
        
        project.getPrivileges().get(appUser.getUsername()).getList().add(privilege);

        repositoryFactory.getProjectPrivilegeList().update(project.getPrivileges().get(appUser.getUsername()));
    }


    @PutMapping("/project/{project_id}/privilege/remove/{user_id}/{privilege}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void removePrivilegeFromProject(
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") String userId,
        @PathVariable("privilege") ProjectPrivilege privilege
    ){
        if(privilege == ProjectPrivilege.MODERATOR){
            throw new RuntimeException();
        }

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        project.getPrivileges().get(appUser.getUsername()).getList().remove(privilege);

        repositoryFactory.getProjectPrivilegeList().update(project.getPrivileges().get(appUser.getUsername()));
    }

    @PutMapping("/project/{project_id}/privilege/add/moderator/{user_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN})
    public void addModeratorToProject(
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") String userId
    ){


        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);
        if(!project.getPrivileges().containsKey(appUser.getUsername())){
            project.getPrivileges().put(appUser.getUsername(), 
                repositoryFactory.getProjectPrivilegeList().create()
            );
            repositoryFactory.getProjectRepository().updateProjectFull(project);
        }
        
        project.getPrivileges().get(appUser.getUsername()).getList().add(ProjectPrivilege.MODERATOR);

        repositoryFactory.getProjectPrivilegeList().update(project.getPrivileges().get(appUser.getUsername()));
    }


    @PutMapping("/project/{project_id}/privilege/remove/moderator/{user_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void removeModeratorFromProject(
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") String userId
    ){
 
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        project.getPrivileges().get(appUser.getUsername()).getList().remove(ProjectPrivilege.MODERATOR);

        repositoryFactory.getProjectPrivilegeList().update(project.getPrivileges().get(appUser.getUsername()));
    }


}
