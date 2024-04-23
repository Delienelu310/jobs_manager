package com.ilumusecase.jobs_manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.Channel;
import com.ilumusecase.jobs_manager.resources.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.resources.ProjectDetails;

@RestController
public class ProjectController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    
    @GetMapping("/projects")
    public List<Project> getAllProjects(){
        return repositoryFactory.getProjectRepository().retrieveAllProjects();
    }

    @GetMapping("/projects/{id}")
    public Project getProjectById(@PathVariable("id") String id){
        return repositoryFactory.getProjectRepository().retrieveProjectById(id);
    }

    @PostMapping("/projects")
    public Project createProject(@RequestBody ProjectDetails projectDetails){
        return repositoryFactory.getProjectRepository().createProject(projectDetails);
    }

    @DeleteMapping("/projects/{id}")
    public void deleteProject(@PathVariable("id") String id){
        repositoryFactory.getProjectRepository().deleteProject(id);
    }

    @PutMapping("/projects/{id}/input/add/{label}")
    public Project addInputChannel(@PathVariable("id") String id, @PathVariable("label") String label, @RequestBody ChannelDetails channelDetails){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(id);
        Channel channel =  repositoryFactory.getChannelsRepository().createChannel(project, channelDetails);

        if(project.getInputChannels().containsKey(label)){
            throw new RuntimeException("The label is already taken");
        }
        project.getInputChannels().put(label, channel);

        return repositoryFactory.getProjectRepository().updateProjectFull(project);
    }

    @PutMapping("/projects/{id}/input/remove/{channel_id}")
    public void removeInputChannel(@PathVariable("id") Long id, @PathVariable("channel_id") Long channelId){

    }

    @PutMapping("/projects/{id}/output/add/{channel_id}")
    public void addOutputChannel(@PathVariable("id") Long id, @PathVariable("channel_id") Long channelId){

    }

    @PutMapping("/projects/{id}/output/remove/{channel_id}")
    public void removeOutputChannel(@PathVariable("id") Long id, @PathVariable("channel_id") Long channelId){

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
