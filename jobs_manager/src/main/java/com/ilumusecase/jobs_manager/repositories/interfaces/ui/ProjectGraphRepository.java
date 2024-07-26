package com.ilumusecase.jobs_manager.repositories.interfaces.ui;

import java.util.Optional;

import com.ilumusecase.jobs_manager.resources.ui.ProjectGraph;

public interface ProjectGraphRepository {

    public Optional<ProjectGraph> retrieveProjectGraphByProjectId(String projectId);
    public ProjectGraph updateProjectGraph(ProjectGraph projectGraph);
}