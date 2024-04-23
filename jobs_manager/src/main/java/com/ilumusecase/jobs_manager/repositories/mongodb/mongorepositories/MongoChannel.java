package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.Channel;

@Repository
public interface MongoChannel extends MongoRepository<Channel, String> {
    @Query("{'projectId': ?0}")
    List<Channel> findByProjectId(String projectId);
    
}
