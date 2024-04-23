package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;

import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.resources.ProjectDetails;

public interface ProjectRepository {


    public List<Project> retrieveAllProjects();
    public Project retrieveProjectById(String id);

    public Project createProject(ProjectDetails projectDetails);
    public Project updateProject(String id, ProjectDetails projectDetails);
    public Project updateProjectFull(Project project);

    public void deleteProject(String id);



} 
