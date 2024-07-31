package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.ilum;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ilumusecase.jobs_manager.resources.ilum.JobResult;


public interface MongoJobResult extends MongoRepository<JobResult, String> {

}
