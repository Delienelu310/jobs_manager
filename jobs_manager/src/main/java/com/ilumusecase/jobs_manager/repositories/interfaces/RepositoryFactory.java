package com.ilumusecase.jobs_manager.repositories.interfaces;

import com.ilumusecase.jobs_manager.resources.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.ProjectPrivilege;

public interface RepositoryFactory {

    public JobNodesRepository getJobNodesRepository();
    public ProjectRepository getProjectRepository();
    public ChannelsRepository getChannelsRepository();
    public ChannelListRepository getChannelListRepository();
    public AppUserRepository getUserDetailsManager();
    public PrivilegeListRepository<ProjectPrivilege> getProjectPrivilegeList();
    public PrivilegeListRepository<JobNodePrivilege> getJobNodePrivilegeList();
} 
