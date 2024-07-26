package com.ilumusecase.jobs_manager.controllers.authorization_controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.constraints.Min;

@RestController
public class PrivilegeController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    


    @GetMapping("/projects/{project_id}/privileges")
    public Object retrievePrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "jobNodeId", defaultValue = "", required = false) String jobNodeId,
        @RequestParam(name = "jobPrivileges", defaultValue = "", required = false) List<JobNodePrivilege> jobNodePrivileges,
        @RequestParam(name = "projectPrivileges", defaultValue = "", required = false) List<ProjectPrivilege> projectPrivileges,

        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber
    ){
        return repositoryFactory.getUserDetailsManager().retrieveProjectPrivileges(
            projectId, 
            query, 
            projectPrivileges, 
            pageSize, 
            pageNumber
        );
    }

    @PutMapping("/project/{project_id}/job_nodes/{job_node_id}/privilege/add/{user_id}/{privilege}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    @AuthorizeJobRoles(roles = JobNodePrivilege.MANAGER)
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
