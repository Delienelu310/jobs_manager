package com.ilumusecase.jobs_manager.repositories.interfaces;

import org.springframework.security.provisioning.UserDetailsManager;

public interface RepositoryFactory {

    public JobNodesRepository getJobNodesRepository();
    public ProjectRepository getProjectRepository();
    public ChannelsRepository getChannelsRepository();
    public ChannelListRepository getChannelListRepository();
    public UserDetailsManager getUserDetailsManager();
} 
