package com.ilumusecase.jobs_manager.repositories.mongodb.ilum;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.ilum.JobResultRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.ilum.MongoJobResult;
import com.ilumusecase.jobs_manager.resources.ilum.JobResult;

@Repository
public class JobResultMongoRepository  implements JobResultRepository{

    @Autowired
    private MongoJobResult mongoJobResult;

    @Override
    public void deleteJobResultById(String id) {
        mongoJobResult.deleteById(id);
    }

    @Override
    public String updateJobResultFull(JobResult jobResult) {
        return mongoJobResult.save(jobResult).getId();
    }

    @Override
    public Optional<JobResult> retrieveByIlumId(String ilumId) {
        return retrieveByIlumId(ilumId);
    }

    @Override
    public List<JobResult> retrieveAll() {
        return mongoJobResult.findAll();
    }
    
}
