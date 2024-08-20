package com.ilumusecase.jobs_manager.controllers.authorization_controllers;

import java.util.List;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthAdminRoleOnly;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;
import com.ilumusecase.jobs_manager.validation.annotations.Username;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@Validated
public class PrivilegeController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    


    @GetMapping("/projects/{project_id}/privileges")
    @JsonMapperRequest(type="full", resource = "AppUser")
    @AuthorizeProjectRoles
    public Object retrieveProjectPrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @RequestParam(name = "query", defaultValue = "", required = false) @Size(max = 50)  String query,
        @RequestParam(name = "projectPrivileges", defaultValue = "", required = false) @Size(max = 20)  List<ProjectPrivilege> projectPrivileges,
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
    @AuthorizeProjectRoles
    public long retrieveProjectPrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @RequestParam(name = "query", defaultValue = "", required = false) @Size(max = 50)  String query,
        @RequestParam(name = "projectPrivileges", defaultValue = "", required = false) @Size(max = 20)  List<ProjectPrivilege> projectPrivileges
    ){
        return repositoryFactory.getUserDetailsManager().retrieveProjectPrivilegesCount(
            projectId, 
            query, 
            projectPrivileges
        );
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges")
    @JsonMapperRequest(type="full", resource = "AppUser")
    @AuthorizeJobRoles
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public Object retrieveJobNodePrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) @Size(max = 50)  String query,
        @RequestParam(name = "jobNodePrivileges", defaultValue = "", required = false) @Size(max = 20)  List<JobNodePrivilege> jobNodePrivileges,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber
    ){

        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
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
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public long retrieveJobNodePrivilegesCount(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) @Size(max = 50)  String query,
        @RequestParam(name = "jobNodePrivileges", defaultValue = "", required = false) @Size(max = 20) List<JobNodePrivilege> jobNodePrivileges
    ){

        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        if(!jobNode.getProject().getId().equals(projectId)) throw new RuntimeException();

        return repositoryFactory.getUserDetailsManager().retrieveJobNodePrivilegesCount(
            jobNodeId, 
            query, 
            jobNodePrivileges
        );
    }

    @GetMapping("/projects/{project_id}/privileges/users/{username}")
    @AuthorizeProjectRoles
    public List<ProjectPrivilege> retrieveJobNodeUserPrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @PathVariable("username") @Username String username
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        return project.getPrivileges().getOrDefault(username, new PrivilegeList<>()).getList();
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges/users/{username}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public List<JobNodePrivilege> retrieveJobNodeUserPrivileges(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("username") @Username String username
    ){

        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        if(!jobNode.getProject().getId().equals(projectId)) throw new RuntimeException();

        return jobNode.getPrivileges().getOrDefault(username, new PrivilegeList<>()).getList();
    }


    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges/users/{user_id}/{privilege}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = JobNodePrivilege.MANAGER)
    public void addPrivilegeToJobNode(
        @ProjectId  @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") @Username String userId,
        @PathVariable("privilege") @NotNull JobNodePrivilege privilege
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
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
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = JobNodePrivilege.MANAGER)
    public void removePrivilegeFromJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") @Username String userId,
        @PathVariable("privilege") @NotNull JobNodePrivilege privilege
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        if( !project.getId().equals(jobNode.getProject().getId())) throw new RuntimeException();

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        if( !jobNode.getPrivileges().containsKey(appUser.getUsername())) throw new RuntimeException();

        jobNode.getPrivileges().get(appUser.getUsername()).getList().remove(privilege);

        repositoryFactory.getJobNodePrivilegeList().update(jobNode.getPrivileges().get(appUser.getUsername()));
    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/privileges/users/{user_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = JobNodePrivilege.MANAGER)
    public void removeUserFromJobNode(
        @ProjectId  @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("user_id") @Username String userId
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        if( !project.getId().equals(jobNode.getProject().getId())) throw new RuntimeException();

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        if( !jobNode.getPrivileges().containsKey(appUser.getUsername())) throw new RuntimeException();

        repositoryFactory.getJobNodePrivilegeList().delete(jobNode.getPrivileges().get(userId).getId());
        jobNode.getPrivileges().remove(userId);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    }

    @PutMapping("/projects/{project_id}/privileges/users/{user_id}/{privilege}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void addPrivilegeToProject(
        @ProjectId @PathVariable("project_id") String projectId,
        @PathVariable("user_id") @Username String userId,
        @PathVariable("privilege") @NotNull ProjectPrivilege privilege
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
        @ProjectId @PathVariable("project_id") String projectId,
        @PathVariable("user_id") @Username String userId,
        @PathVariable("privilege") @NotNull ProjectPrivilege privilege
    ){
        if(privilege == ProjectPrivilege.MODERATOR){
            throw new RuntimeException();
        }

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        AppUser appUser = repositoryFactory.getUserDetailsManager().retrieveUserById(userId);

        project.getPrivileges().get(appUser.getUsername()).getList().remove(privilege);

        repositoryFactory.getProjectPrivilegeList().update(project.getPrivileges().get(appUser.getUsername()));
    }

    @DeleteMapping("/projects/{project_id}/privileges/users/{user_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void removeUserFromProject(
        @ProjectId @PathVariable("project_id") String projectId,
        @PathVariable("user_id") @Username String userId
    ){
       
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        if(!project.getPrivileges().containsKey(userId)){
            throw new RuntimeException();
        }
        if(project.getPrivileges().get(userId).getList().contains(ProjectPrivilege.ADMIN)) throw new RuntimeException();
        if(project.getPrivileges().get(userId).getList().contains(ProjectPrivilege.MODERATOR)) throw new RuntimeException();

        
        repositoryFactory.getProjectPrivilegeList().delete(project.getPrivileges().get(userId).getId());
        project.getPrivileges().remove(userId);
        repositoryFactory.getProjectRepository().updateProjectFull(project);
    }
    @DeleteMapping("/projects/{project_id}/privileges/moderators/{user_id}")
    @AuthAdminRoleOnly
    public void removeModeratorFromProject( 
        @PathVariable("project_id") String projectId,
        @PathVariable("user_id") @Username String userId
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);

        if(!project.getPrivileges().containsKey(userId)){
            throw new RuntimeException();
        }
        if(project.getPrivileges().get(userId).getList().contains(ProjectPrivilege.ADMIN)) throw new RuntimeException();
        if(!project.getPrivileges().get(userId).getList().contains(ProjectPrivilege.MODERATOR)) throw new RuntimeException();

        
        repositoryFactory.getProjectPrivilegeList().delete(project.getPrivileges().get(userId).getId());
        project.getPrivileges().remove(userId);
        repositoryFactory.getProjectRepository().updateProjectFull(project);

    }


    @PutMapping("/projects/{project_id}/privileges/moderators/{user_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN})
    public void addModeratorToProject(
        @ProjectId @PathVariable("project_id") String projectId,
        @PathVariable("user_id") @Username String userId
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
}
