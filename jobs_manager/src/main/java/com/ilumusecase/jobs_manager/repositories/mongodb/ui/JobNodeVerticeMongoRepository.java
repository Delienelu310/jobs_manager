package com.ilumusecase.jobs_manager.repositories.mongodb.ui;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.ui.JobNodeVerticeRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.ui.MongoJobNodeVertice;
import com.ilumusecase.jobs_manager.resources.ui.JobNodeVertice;

@Repository
public class JobNodeVerticeMongoRepository implements JobNodeVerticeRepository{

    @Autowired
    private MongoJobNodeVertice mongoJobNodeVertice;

    @Override
    public JobNodeVertice updateJobNodeVertice(JobNodeVertice jobNodeVertice) {
        return mongoJobNodeVertice.save(jobNodeVertice);
    }

    @Override
    public Optional<JobNodeVertice> retrieveById(String id) {
        return mongoJobNodeVertice.findById(id);
    }

    @Override
    public Optional<JobNodeVertice> retrieveByJobNodeId(String jobNodeId) {
        return mongoJobNodeVertice.findByJobNode_Id(jobNodeId);
    }
    
}
