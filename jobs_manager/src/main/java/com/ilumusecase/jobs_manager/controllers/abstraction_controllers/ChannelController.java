package com.ilumusecase.jobs_manager.controllers.abstraction_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.channelLaunchers.ChannelLauncherFactory;
import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.Channel;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.DisableDefaultAuth;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class ChannelController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;
    @Autowired
    private ChannelLauncherFactory channelLauncherFactory;


    @GetMapping("/channels")
    @DisableDefaultAuth
    public MappingJacksonValue retrieveAllChannels(){
        return jsonMappersFactory.getChannelJsonMapper().getFullChannelList(
            repositoryFactory.getChannelsRepository().retrieveAll()
        );
    }

    @GetMapping("/projects/{project_id}/channels")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public MappingJacksonValue retreiveChannelsByProjectId(@ProjectId @PathVariable("project_id") String projectId){

        return jsonMappersFactory.getChannelJsonMapper().getFullChannelList(
            repositoryFactory.getChannelsRepository().retrieveAllByProjectId(projectId)
        );

    }

    @GetMapping("/projects/{project_id}/channels/{channel_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public MappingJacksonValue retrieveChannel(@ProjectId @PathVariable("project_id") String projectId, @PathVariable("channel_id") String channelId){
        Channel channel = repositoryFactory.getChannelsRepository().retrieveById(channelId);
        if( !channel.getProject().getId().equals(projectId) ){
            throw new RuntimeException("The channel does not belong to the project with id " + projectId);
        }

        return jsonMappersFactory.getChannelJsonMapper().getFullChannel(channel);
    }

    @PostMapping("/projects/{project_id}/channels")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public MappingJacksonValue createChannel(@ProjectId @PathVariable("project_id") String projectId, @RequestBody ChannelDetails channelDetails){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        return jsonMappersFactory.getChannelJsonMapper().getFullChannel(
            repositoryFactory.getChannelsRepository().createChannel(project, channelDetails)
        );
        
    }

    @DeleteMapping("/projects/{project_id}/channels/{channel_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void deleteChannelById(@ProjectId @PathVariable("project_id") String projectId, @PathVariable("channel_id") String channelId){
        // remove channel from input and output of project
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        for(String label : project.getInputChannels().keySet()){
            if(project.getInputChannels().get(label).getId().equals(channelId)){

                project.getInputChannels().remove(label);
            }
        }

        for(String label : project.getOutputChannels().keySet()){
            if(project.getOutputChannels().get(label).getId().equals(channelId)){
                project.getOutputChannels().remove(label);
            }
        }


        project.getChannels().removeIf(ch -> ch.getId().equals(channelId));
        repositoryFactory.getProjectRepository().updateProjectFull(project);

        // remove from job nodes
        Channel channel = repositoryFactory.getChannelsRepository().retrieveById(channelId);
        for(JobNode jobNode : channel.getInputJobs()){
            for(String label : jobNode.getOutput().keySet()){
                jobNode.getOutput().get(label).getChannelList().removeIf(ch -> ch.getId().equals(channelId));
                repositoryFactory.getChannelListRepository().update(jobNode.getOutput().get(label));
            }
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
        }
        for(JobNode jobNode : channel.getOutputJobs()){
            for(String label : jobNode.getInput().keySet()){
                jobNode.getInput().get(label).getChannelList().removeIf(ch -> ch.getId().equals(channelId));
                repositoryFactory.getChannelListRepository().update(jobNode.getInput().get(label));
            }
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
        }

        repositoryFactory.getChannelsRepository().deleteChannelById(channelId);
    }

    @PutMapping("/projects/{project_id}/channels/{channel_id}/start")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void startChannel(@ProjectId @PathVariable("project_id") String projectId, @PathVariable("channel_id") String channelId){
        Channel channel = repositoryFactory.getChannelsRepository().retrieveById(channelId);
        if( !channel.getProject().getId().equals(projectId) ){
            throw new RuntimeException();
        }

        channelLauncherFactory.getChannelLauncher(channel.getChannelDetails().getType())
            .launchChannel(channel);
    }

    @PutMapping("/projects/{project_id}/channels/{channel_id}/stop")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    public void stopChannel(@ProjectId @PathVariable("project_id") String projectId, @PathVariable("channel_id") String channelId){
        Channel channel = repositoryFactory.getChannelsRepository().retrieveById(channelId);
        if( !channel.getProject().getId().equals(projectId) ){
            throw new RuntimeException();
        }

        channelLauncherFactory.getChannelLauncher(channel.getChannelDetails().getType())
            .stopChannel(channel);
    }

}
