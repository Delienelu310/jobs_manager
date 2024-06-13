package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ilumusecase.jobs_manager.resources.JobsFile;

public interface MongoJobsFile extends MongoRepository<JobsFile, String> {

    @Query("{ 'jobNode.id' : ?0 }")
    public List<JobsFile> findByJobNodeId(String id);

    @Query("{ 'author.username' : ?0 }")
    public List<JobsFile> findByAuthorUsername(String username);
}
