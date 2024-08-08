package com.ilumusecase.jobs_manager.repositories.interfaces.ui;

import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.ui.ProjectGraph;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface ProjectGraphRepository {

    public Optional<ProjectGraph> retrieveProjectGraphByProjectId(String projectId);
    public ProjectGraph updateProjectGraph(@Valid @NotNull ProjectGraph projectGraph);
}