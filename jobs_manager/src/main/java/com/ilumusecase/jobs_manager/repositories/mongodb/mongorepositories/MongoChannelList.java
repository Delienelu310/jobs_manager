package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.ChannelList;

@Repository
public interface MongoChannelList extends MongoRepository<ChannelList, String>{
    
}
