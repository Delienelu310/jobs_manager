package com.ilumusecase.jobs_manager.repositories.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.AppUserRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.ChannelListRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.ChannelsRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.JobNodesRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.ProjectRepository;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoAppUser;

@Repository
public class MongoRepository implements RepositoryFactory{

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
        return new MongoUserDetailsManager(mongoAppUser);
    }

  
    
}
