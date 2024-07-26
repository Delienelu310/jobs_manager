package com.ilumusecase.jobs_manager.repositories.interfaces;

import com.ilumusecase.jobs_manager.repositories.interfaces.abstraction.ChannelListRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.abstraction.ChannelsRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.abstraction.JobNodesRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.abstraction.ProjectRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.authorization.AppUserRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.authorization.PrivilegeListRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.ilum.IlumGroupRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.ilum.JobRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.ilum.JobScriptRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.ilum.JobsFileRepositoryInterface;
import com.ilumusecase.jobs_manager.repositories.interfaces.ui.JobNodeVerticeRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.ui.ProjectGraphRepository;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;

public interface RepositoryFactory {

    public JobNodesRepository getJobNodesRepository();
    public ProjectRepository getProjectRepository();
    public ChannelsRepository getChannelsRepository();
    public ChannelListRepository getChannelListRepository();
    public AppUserRepository getUserDetailsManager();
    public PrivilegeListRepository<ProjectPrivilege> getProjectPrivilegeList();
    public PrivilegeListRepository<JobNodePrivilege> getJobNodePrivilegeList();
    public JobRepository getJobRepository();
    public IlumGroupRepository getIlumGroupRepository();
    public JobsFileRepositoryInterface getJobsFileRepositoryInterface();
    public JobScriptRepository getJobScriptRepository();


    public ProjectGraphRepository getProjectGraphRepository();
    public JobNodeVerticeRepository getJobNodeVerticeRepository();
} 
