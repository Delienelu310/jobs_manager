package com.ilumusecase.jobs_manager.repositories.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
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
import com.ilumusecase.jobs_manager.repositories.mongodb.abstraction.ChannelListMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.abstraction.ChannelsMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.abstraction.JobNodesMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.abstraction.ProjectMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.authorization.JobNodePrivilegeListMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.authorization.MongoUserDetailsManager;
import com.ilumusecase.jobs_manager.repositories.mongodb.authorization.ProjectPrivilegeListMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.ilum.JobMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.ilum.JobScriptsMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.ilum.JobsFileMongo;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.authorization.MongoAppUser;
import com.ilumusecase.jobs_manager.repositories.mongodb.ui.JobNodeVerticeMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.ui.ProjectGraphMongoRepository;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;

@Repository
public class MongoRepository implements RepositoryFactory{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JobNodesMongoRepository jobNodesRepository;
    @Autowired
    private ProjectMongoRepository projectMongoRepository;
    @Autowired
    private ChannelsMongoRepository channelsRepository;
    @Autowired
    private ChannelListMongoRepository channelListMongoRepository;
    @Autowired
    private MongoAppUser mongoAppUser;
    @Autowired
    private ProjectPrivilegeListMongoRepository projectPrivilegeListMongoRepository;
    @Autowired
    private JobNodePrivilegeListMongoRepository jobNodePrivilegeListMongoRepository;
    @Autowired
    private JobMongoRepository jobMongoRepository;
    @Autowired
    private IlumGroupRepository ilumGroupRepository;
    @Autowired
    private JobsFileMongo jobsFileMongo;
    @Autowired
    private JobScriptsMongoRepository jobScriptsMongoRepository;



    @Autowired
    private ProjectGraphMongoRepository projectGraphMongoRepository;
    @Autowired
    private JobNodeVerticeMongoRepository jobNodeVerticeMongoRepository;


    @Override
    public JobNodesRepository getJobNodesRepository() {
        return this.jobNodesRepository;
    }

    @Override
    public ProjectRepository getProjectRepository() {
        return this.projectMongoRepository;
    }

    @Override
    public ChannelsRepository getChannelsRepository() {
        return this.channelsRepository;
    }

    @Override
    public ChannelListRepository getChannelListRepository() {
        return this.channelListMongoRepository;
    }

    @Override
    public AppUserRepository getUserDetailsManager() {
        return new MongoUserDetailsManager(mongoAppUser, mongoTemplate);
    }

    @Override
    public PrivilegeListRepository<ProjectPrivilege> getProjectPrivilegeList() {
        return this.projectPrivilegeListMongoRepository;
    }

    @Override
    public PrivilegeListRepository<JobNodePrivilege> getJobNodePrivilegeList() {
        return this.jobNodePrivilegeListMongoRepository;
    }

    @Override
    public JobRepository getJobRepository() {
        return this.jobMongoRepository;
    }

    @Override
    public IlumGroupRepository getIlumGroupRepository() {
        return this.ilumGroupRepository;
    }

    @Override
    public JobsFileRepositoryInterface getJobsFileRepositoryInterface() {
        return this.jobsFileMongo;
    }

    @Override
    public JobScriptRepository getJobScriptRepository() {
        return jobScriptsMongoRepository;
    }

    @Override
    public ProjectGraphRepository getProjectGraphRepository() {
        return projectGraphMongoRepository;
    }

    @Override
    public JobNodeVerticeRepository getJobNodeVerticeRepository() {
        return jobNodeVerticeMongoRepository;
    }

  
    
}
