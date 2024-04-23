package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;

import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.resources.ProjectDetails;

public interface ProjectRepository {


    public List<Project> retrieveAllProjects();
    public Project retrieveProjectById(Long id);

    public Project createProject(ProjectDetails projectDetails);

    public void deleteProject(Long id);



} 
