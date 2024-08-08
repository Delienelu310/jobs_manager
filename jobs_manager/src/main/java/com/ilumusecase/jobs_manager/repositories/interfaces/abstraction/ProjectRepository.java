package com.ilumusecase.jobs_manager.repositories.interfaces.abstraction;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.abstraction.ProjectDetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Validated
public interface ProjectRepository {


    public List<Project> retrieveProjectsFiltered(
        @Min(1) int pageSize,
        @Min(0) int pageNumber,
        @Size(max = 50) String query,
        @Size(max = 50) String username,
        @Size(max = 50) String admin
    );
    public long countProjectsFiltered(@Size(max = 50) String query, @Size(max = 50) String username, @Size(max = 50) String admin);

    public List<Project> retrieveAllProjects();
    public Project retrieveProjectById(String id);

    public Project createProject(@Valid @NotNull ProjectDetails projectDetails);
    public Project updateProject(String id, @NotNull @Valid ProjectDetails projectDetails);
    public Project updateProjectFull(@NotNull @Valid Project project);

    public void deleteProject(String id);



} 
