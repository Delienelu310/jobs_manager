package com.ilumusecase.jobs_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.Channel;
import com.ilumusecase.jobs_manager.resources.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.Project;

@RestController
public class ChannelController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;


    @GetMapping("/channels")
    public MappingJacksonValue retrieveAllChannels(){
        return jsonMappersFactory.getChannelJsonMapper().getFullChannelList(
            repositoryFactory.getChannelsRepository().retrieveAll()
        );
    }

    @GetMapping("/projects/{project_id}/channels")
    public MappingJacksonValue retreiveChannelsByProjectId(@PathVariable("project_id") String projectId){

        return jsonMappersFactory.getChannelJsonMapper().getFullChannelList(
            repositoryFactory.getChannelsRepository().retrieveAllByProjectId(projectId)
        );

    }

    @GetMapping("/projects/{project_id}/channels/{channel_id}")
    public MappingJacksonValue retrieveChannel(@PathVariable("project_id") String projectId, @PathVariable("channel_id") String channelId){
        Channel channel = repositoryFactory.getChannelsRepository().retrieveById(channelId);
        if( !channel.getProject().getId().equals(projectId) ){
            throw new RuntimeException("The channel does not belong to the project with id " + projectId);
        }

        return jsonMappersFactory.getChannelJsonMapper().getFullChannel(channel);
    }

    @PostMapping("/projects/{project_id}/channels")
    public MappingJacksonValue createChannel(@PathVariable("project_id") String projectId, @RequestBody ChannelDetails channelDetails){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        return jsonMappersFactory.getChannelJsonMapper().getFullChannel(
            repositoryFactory.getChannelsRepository().createChannel(project, channelDetails)
        );
        
    }

    @DeleteMapping("/projects/{project_id}/channels/{channel_id}")
    public void deleteChannelById(@PathVariable("project_id") String projectId, @PathVariable("channel_id") String channelId){
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

}
