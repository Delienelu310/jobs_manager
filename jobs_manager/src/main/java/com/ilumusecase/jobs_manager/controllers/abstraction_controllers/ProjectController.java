package com.ilumusecase.jobs_manager.controllers.abstraction_controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.Channel;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.abstraction.ProjectDetails;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.security.Roles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.IgnoreAuthAspect;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.constraints.Min;

@RestController
public class ProjectController {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @Autowired
    private ChannelController channelController;
    

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);


    @GetMapping("/projects")
    @JsonMapperRequest(type="simple", resource = "Project")
    public Object getProjects(
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "admin", required = false) String admin,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber,
        Authentication authentication
    ){
        logger.info(query + " " + pageSize + " " + pageNumber + " " + authentication.getName());

        query = query.trim();

        return repositoryFactory.getProjectRepository().retrieveProjectsFiltered(pageSize, pageNumber, query, authentication.getName(), admin);
       
    }

    @GetMapping("/projects/all")
    @JsonMapperRequest(type="simple", resource = "Project")
    public Object getAllProjects(Authentication authentication){

        //if the user if admin of the whole application, return all of the projects
        if(authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_MODERATOR"))){
            return repositoryFactory.getProjectRepository().retrieveAllProjects();
        }

        //otherwise return the list of projects, that user has access to

        String username = authentication.getName();
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(username);


        return repositoryFactory.getProjectRepository().retrieveAllProjects().stream().filter(project -> 
            project.getPrivileges().keySet().contains(appUser.getUsername())
        ).toList();

        
        
    }

    @GetMapping("/projects/{id}")
    @IgnoreAuthAspect
    @JsonMapperRequest(resource = "Project", type="full")
    public Object getProjectById(Authentication authentication, @PathVariable("id") String id){

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);

        if(
            !authentication.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_MODERATOR") || auth.toString().equals("ROLE_ADMIN"))
            &&
            !project.getPrivileges().containsKey(authentication.getName())
        ){
            throw new RuntimeException("User cannot access current object");
        }

        return project;
    }

    @PostMapping("/projects")
    @AuthorizeRoles(roles={Roles.MANAGER})
    @JsonMapperRequest(type="full", resource = "Project")
    public Object createProject(Authentication authentication, @RequestBody ProjectDetails projectDetails){

        // add user as adming of the project
        Project project = repositoryFactory.getProjectRepository().createProject(projectDetails);

        PrivilegeList<ProjectPrivilege> privilegeList = repositoryFactory.getProjectPrivilegeList().create();
        privilegeList.getList().add(ProjectPrivilege.ADMIN);
        repositoryFactory.getProjectPrivilegeList().update(privilegeList);

        project.setAdmin(authentication.getName());
        project.getPrivileges().put(authentication.getName(), privilegeList);
        project = repositoryFactory.getProjectRepository().updateProjectFull(project);

        return project;
    }

    @DeleteMapping("/projects/{id}")
    @AuthorizeProjectRoles(roles={ProjectPrivilege.ADMIN})
    public void deleteProject( @ProjectId @PathVariable("id") String id){

        repositoryFactory.getProjectRepository().deleteProject(id);
    }

    @PutMapping("/projects/{id}/input/add/{label}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    @JsonMapperRequest(type="full", resource = "Project")
    public Object addInputChannel(@ProjectId @PathVariable("id") String id, @PathVariable("label") String label, @RequestBody ChannelDetails channelDetails){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);
        Channel channel =  repositoryFactory.getChannelsRepository().createChannel(project, channelDetails);

        if(project.getInputChannels().containsKey(label) && project.getInputChannels().get(label) != null){
            throw new RuntimeException("The label is already taken");
        }
        project.getInputChannels().put(label, channel);
        project.getChannels().add(channel);

        return repositoryFactory.getProjectRepository().updateProjectFull(project);
    }

    @PutMapping("/projects/{id}/input/remove/{label}")
    @AuthorizeProjectRoles(roles= { ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR })
    @JsonMapperRequest(type="full", resource = "Project")
    public Object removeInputChannel(@ProjectId @PathVariable("id") String id, @PathVariable("label") String label){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);

        if(  !project.getInputChannels().containsKey(label) || project.getInputChannels().get(label) == null){
            throw new RuntimeException();
        }
        Channel channel = project.getInputChannels().get(label);

        channelController.deleteChannelById(id, channel.getId());
        
        return repositoryFactory.getProjectRepository().retrieveProjectById(id);

    }

    @PutMapping("/projects/{id}/output/add/{label}")
    @AuthorizeProjectRoles(roles= { ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR })
    @JsonMapperRequest(type="full", resource = "Project")
    public Object addOutputChannel(@ProjectId @PathVariable("id") String id, @PathVariable("label") String label, @RequestBody ChannelDetails channelDetails){

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);
        Channel channel =  repositoryFactory.getChannelsRepository().createChannel(project, channelDetails);

        if(project.getOutputChannels().containsKey(label) && project.getOutputChannels().get(label) != null){
            throw new RuntimeException("The label is already taken");
        }
        project.getOutputChannels().put(label, channel);
        project.getChannels().add(channel);

        return repositoryFactory.getProjectRepository().updateProjectFull(project);
        
    }

    @PutMapping("/projects/{id}/output/remove/{label}")
    @AuthorizeProjectRoles(roles= { ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR })
    @JsonMapperRequest(type="full", resource = "Project")
    public Object removeOutputChannel(@ProjectId @PathVariable("id") String id, @PathVariable("label") String label){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);

        if(  !project.getOutputChannels().containsKey(label) || project.getOutputChannels().get(label) == null){
            throw new RuntimeException();
        }
        Channel channel = project.getOutputChannels().get(label);

        channelController.deleteChannelById(id, channel.getId());
        
        return repositoryFactory.getProjectRepository().retrieveProjectById(id);
        

    }

    @PutMapping("/projects/{id}/start/channels")
    @AuthorizeProjectRoles(roles= { ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR })
    public void startChannels(@ProjectId @PathVariable("id") String id){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);
        for(Channel channel : project.getChannels()){
            channelController.startChannel(id, channel.getId());
        }
    }

    @PutMapping("/projects/{id}/stop/channels")
    @AuthorizeProjectRoles(roles= { ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR })
    public void stopChannels(@ProjectId @PathVariable("id") String id){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);
        for(Channel channel : project.getChannels()){
            channelController.stopChannel(id, channel.getId());
        }
    }

    @PutMapping("/projects/{id}")
    @AuthorizeProjectRoles(roles= { ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR })
    @JsonMapperRequest(type="full", resource = "Project")
    public Object updateProject(@ProjectId @PathVariable("id") String id, @RequestBody ProjectDetails projectDetails){
        return repositoryFactory.getProjectRepository().updateProject(id, projectDetails);
    }


}
