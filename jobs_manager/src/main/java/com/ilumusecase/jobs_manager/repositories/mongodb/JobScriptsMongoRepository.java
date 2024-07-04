package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.JobScriptRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoJobScripts;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

@Repository
public class JobScriptsMongoRepository implements JobScriptRepository{


    @Autowired
    private MongoJobScripts mongoJobScripts;

    @Override
    public List<JobScript> retrieveAllJobScripts() {
        return mongoJobScripts.findAll();
    }

    @Override
    public Optional<JobScript> retrieveJobScriptById(String id) {
        return mongoJobScripts.findById(id);
    }

    @Override
    public JobScript updateFullJobScript(JobScript jobScript) {
        return mongoJobScripts.save(jobScript);
    }

    @Override
    public void deleteJobScript(String id) {
        mongoJobScripts.deleteById(id);
    }

    @Override
    public List<JobScript> retrieveJobScriptsByJobsFileId(String jobsFileId) {
        return mongoJobScripts.retrieveByJobsFileId(jobsFileId);
    }

    @Override
    public List<JobScript> retrieveJobScriptsByJobNodeId(String jobNodeId) {
        return mongoJobScripts.retrieveByJobNodeId(jobNodeId);
    }
    
}
