package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.ui;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ilumusecase.jobs_manager.resources.ui.JobNodeVertice;

public interface MongoJobNodeVertice extends MongoRepository<JobNodeVertice, String>{
    Optional<JobNodeVertice> findByJobNode_Id(String jobNodeId);
}
