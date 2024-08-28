package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.ilum;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

@Repository
public interface MongoJobEntity extends MongoRepository<JobEntity, String>{
    @Query("{ 'jobScript.id': ?0 }")
    public List<JobEntity> retrieveByJobScriptId(String jobScriptId);

    public void deleteByJobNodeId(String jobNodeId);

}
