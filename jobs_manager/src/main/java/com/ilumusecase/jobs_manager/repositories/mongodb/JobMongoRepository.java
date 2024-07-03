package com.ilumusecase.jobs_manager.repositories.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.JobRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoJobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

@Repository
public class JobMongoRepository implements JobRepository{
    
    @Autowired
    private MongoJobEntity mongoJobEntity;

    @Override
    public JobEntity retrieveJobEntity(String id) {
        return mongoJobEntity.findById(id).get();
    }

    @Override
    public void deleteJob(String id) {
        mongoJobEntity.deleteById(id);
    }

    @Override
    public JobEntity updateJobFull(JobEntity jobEntity) {
        return mongoJobEntity.save(jobEntity);
    }

    
}
