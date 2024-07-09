package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;

import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.abstraction.ProjectDetails;

public interface ProjectRepository {


    public List<Project> retrieveProjectsFiltered(
        int pageSize,
        int pageNumber,
        String query,
        String username,
        String admin
    );

    public List<Project> retrieveAllProjects();
    public Project retrieveProjectById(String id);

    public Project createProject(ProjectDetails projectDetails);
    public Project updateProject(String id, ProjectDetails projectDetails);
    public Project updateProjectFull(Project project);

    public void deleteProject(String id);



} 
