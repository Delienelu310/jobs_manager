package com.ilumusecase.jobs_manager.controllers.authorization_controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.constraints.Min;

@RestController
public class PrivilegeController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    


    @GetMapping("/projects/{project_id}/privileges")
    @JsonMapperRequest(type="full", resource = "AppUser")
    public Object retrieveProjectPrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
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


    @GetMapping("/projects/{project_id}/privileges/count")
    public long retrieveProjectPrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "projectPrivileges", defaultValue = "", required = false) List<ProjectPrivilege> projectPrivileges
    ){
        return repositoryFactory.getUserDetailsManager().retrieveProjectPrivilegesCount(
            projectId, 
            query, 
            projectPrivileges
        );
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges")
    @JsonMapperRequest(type="full", resource = "AppUser")
    public Object retrieveJobNodePrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @ProjectId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "jobNodePrivileges", defaultValue = "", required = false) List<JobNodePrivilege> jobNodePrivileges,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber
    ){

        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if(!jobNode.getProject().getId().equals(projectId)) throw new RuntimeException();


        return repositoryFactory.getUserDetailsManager().retrieveJobNodePrivileges(
            jobNodeId, 
            query, 
            jobNodePrivileges, 
            pageSize, 
            pageNumber
        );
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges/count")
    public long retrieveJobNodePrivilegesCount(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "jobNodePrivileges", defaultValue = "", required = false) List<JobNodePrivilege> jobNodePrivileges
    ){

        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if(!jobNode.getProject().getId().equals(projectId)) throw new RuntimeException();

        return repositoryFactory.getUserDetailsManager().retrieveJobNodePrivilegesCount(
            jobNodeId, 
            query, 
            jobNodePrivileges
        );
    }

    @GetMapping("/projects/{project_id}/privileges/users/{username}")
    public List<ProjectPrivilege> retrieveJobNodeUserPrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @PathVariable("username") String username
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        return project.getPrivileges().getOrDefault(username, new PrivilegeList<>()).getList();
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges/users/{username}")
    public List<JobNodePrivilege> retrieveJobNodeUserPrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @ProjectId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("username") String username
    ){

        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if(!jobNode.getProject().getId().equals(projectId)) throw new RuntimeException();

        return jobNode.getPrivileges().getOrDefault(username, new PrivilegeList<>()).getList();
    }


    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges/users/{user_id}/{privilege}")
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


    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges/users/{user_id}/{privilege}")
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

    @PutMapping("/projects/{project_id}/privileges/users/{user_id}/{privilege}")
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


    @DeleteMapping("/projects/{project_id}/privileges/users/{user_id}/{privilege}")
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

    @PutMapping("/projects/{project_id}/privileges/moderators/{user_id}")
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


    @PutMapping("/projects/{project_id}/privileges/moderators/{user_id}/MODERATOR")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN})
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
