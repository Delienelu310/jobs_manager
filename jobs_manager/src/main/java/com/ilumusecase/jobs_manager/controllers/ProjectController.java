package com.ilumusecase.jobs_manager.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.Channel;
import com.ilumusecase.jobs_manager.resources.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.resources.ProjectDetails;

@RestController
public class ProjectController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;
    
    @GetMapping("/projects")
    public MappingJacksonValue getAllProjects(){
        return jsonMappersFactory.getProjectJsonMapper().getFullProjectList(
            repositoryFactory.getProjectRepository().retrieveAllProjects()
        );
    }

    @GetMapping("/projects/{id}")
    public MappingJacksonValue getProjectById(@PathVariable("id") String id){
        return jsonMappersFactory.getProjectJsonMapper().getFullProject(
            repositoryFactory.getProjectRepository().retrieveProjectById(id)
        );
    }

    @PostMapping("/projects")
    public MappingJacksonValue createProject(@RequestBody ProjectDetails projectDetails){
        return jsonMappersFactory.getProjectJsonMapper().getFullProject(
            repositoryFactory.getProjectRepository().createProject(projectDetails)
        );
    }

    @DeleteMapping("/projects/{id}")
    public void deleteProject(@PathVariable("id") String id){
        repositoryFactory.getProjectRepository().deleteProject(id);
    }

    @PutMapping("/projects/{id}/input/add/{label}")
    public MappingJacksonValue addInputChannel(@PathVariable("id") String id, @PathVariable("label") String label, @RequestBody ChannelDetails channelDetails){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);
        Channel channel =  repositoryFactory.getChannelsRepository().createChannel(project, channelDetails);

        if(project.getInputChannels().containsKey(label)){
            throw new RuntimeException("The label is already taken");
        }
        project.getInputChannels().put(label, channel);

        return jsonMappersFactory.getProjectJsonMapper().getFullProject(
            repositoryFactory.getProjectRepository().updateProjectFull(project)
        );
    }

    @PutMapping("/projects/{id}/input/remove/{channel_id}")
    public MappingJacksonValue removeInputChannel(@PathVariable("id") String id, @PathVariable("channel_id") Long channelId){
        return null;
    }

    @PutMapping("/projects/{id}/output/add/{channel_id}")
    public MappingJacksonValue addOutputChannel(@PathVariable("id") String id, @PathVariable("label") String label, @RequestBody ChannelDetails channelDetails){

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);
        Channel channel =  repositoryFactory.getChannelsRepository().createChannel(project, channelDetails);

        if(project.getOutputChannels().containsKey(label)){
            throw new RuntimeException("The label is already taken");
        }
        project.getOutputChannels().put(label, channel);

        return jsonMappersFactory.getProjectJsonMapper().getFullProject(
            repositoryFactory.getProjectRepository().updateProjectFull(project)
        );
    }

    @PutMapping("/projects/{id}/output/remove/{channel_id}")
    public MappingJacksonValue removeOutputChannel(@PathVariable("id") String id, @PathVariable("channel_id") String channelId){
        return null;
    }

    @PutMapping("/projects/{id}/start/channels")
    public void startChannels(@PathVariable("id") Long id){

    }

    @PutMapping("/projects/{id}/stop/channels")
    public void stopChannels(@PathVariable("id") Long id){

    }

    @PutMapping("/projects/{id}")
    public Project updateProject(@PathVariable("id") String id, ProjectDetails projectDetails){
        return repositoryFactory.getProjectRepository().updateProject(id, projectDetails);
    }


}
