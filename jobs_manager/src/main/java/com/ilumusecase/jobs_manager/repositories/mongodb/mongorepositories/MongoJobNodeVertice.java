package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ilumusecase.jobs_manager.resources.ui.JobNodeVertice;

public interface MongoJobNodeVertice extends MongoRepository<JobNodeVertice, String>{
    
}
