package com.ilumusecase.jobs_manager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
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
    public Project getProjectById(@PathVariable("id") Long id){
        return repositoryFactory.getProjectRepository().retrieveProjectById(id);
    }

    @PostMapping("/projects")
    public Project createProject(ProjectDetails projectDetails){
        return repositoryFactory.getProjectRepository().createProject(projectDetails);
    }

    @DeleteMapping("/projects/{id}")
    public void deleteProject(@PathVariable("id") Long id){
        repositoryFactory.getProjectRepository().deleteProject(id);
    }

    @PutMapping("/projects/{id}/input/add/{channel_id}")
    public void addInputChannel(@PathVariable("id") Long id, @PathVariable("channel_id") Long channelId){

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

    @PutMapping("/projects/{id}/output/remove/{channel_id}")
    public void stopChannels(@PathVariable("id") Long id){

    }

    @PutMapping("/projects/{id}")
    public void updateProject(@PathVariable("id") Long id){
    }


}
