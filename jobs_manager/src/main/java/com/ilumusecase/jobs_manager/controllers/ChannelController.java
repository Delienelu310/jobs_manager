package com.ilumusecase.jobs_manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.Channel;
import com.ilumusecase.jobs_manager.resources.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.Project;

@RestController
public class ChannelController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;

    @GetMapping("/channels")
    public List<Channel> retrieveAllChannels(){
        return repositoryFactory.getChannelsRepository().retrieveAll();
    }

    @GetMapping("/projects/{project_id}/channels")
    public List<Channel> retreiveChannelsByProjectId(@PathVariable("project_id") String projectId){
        return repositoryFactory.getChannelsRepository().retrieveAllByProjectId(projectId);
    }

    @GetMapping("/projects/{project_id}/channels/{channel_id}")
    public Channel retrieveChannel(@PathVariable("project_id") String projectId, @PathVariable("channel_id") String channelId){
        Channel channel = repositoryFactory.getChannelsRepository().retrieveById(channelId);
        if( !channel.getProject().getId().equals(projectId) ){
            throw new RuntimeException("The channel does not belong to the project with id " + projectId);
        }

        return channel;
    }

    @PostMapping("/projects/{project_id}/channels")
    public Channel createChannel(@PathVariable("project_id") String projectId, @RequestBody ChannelDetails channelDetails){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        return repositoryFactory.getChannelsRepository().createChannel(project, channelDetails);
    }

    @DeleteMapping("/channels/{id}")
    public void deleteChannelById(@PathVariable("id") String id){
        repositoryFactory.getChannelsRepository().deleteChannelById(id);
    }

}
