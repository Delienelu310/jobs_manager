package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.Project;

@Repository
public interface MongoProject extends MongoRepository<Project, String>{

    
}
