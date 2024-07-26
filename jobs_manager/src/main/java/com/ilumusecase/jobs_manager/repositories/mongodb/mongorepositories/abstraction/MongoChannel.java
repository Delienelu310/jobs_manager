package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.abstraction;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.abstraction.Channel;

@Repository
public interface MongoChannel extends MongoRepository<Channel, String> {
    @Query("{'project.id': ?0}")
    List<Channel> findByProjectId(String projectId);
    
}
