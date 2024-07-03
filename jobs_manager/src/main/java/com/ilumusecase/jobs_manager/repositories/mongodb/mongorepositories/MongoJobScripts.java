package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

@Repository
public interface MongoJobScripts extends MongoRepository<JobScript, String> {
    
    @Query("{ 'jobsFiles.id': ?0 }")
    public List<JobScript> retrieveByJobsFileId(String jobsFileId);
}
