package com.ilumusecase.jobs_manager.repositories.interfaces;

public interface RepositoryFactory {

    public JobNodesRepository getJobNodesRepository();
    public ProjectRepository getProjectRepository();
    public ChannelsRepository getChannelsRepository();
} 
