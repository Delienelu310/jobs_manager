package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.JobNodesRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoJobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNodeDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

@Repository
public class JobNodesMongoRepository implements JobNodesRepository{

    @Autowired
    private MongoJobNode mongoJobNode;

    @Override
    public List<JobNode> retrieveByProjectId(String projectId) {
        return mongoJobNode.retrieveByProjectId(projectId);
    }

    @Override
    public JobNode retrieveById(String id) {
        return mongoJobNode.findById(id).get();
    }

    @Override
    public List<JobNode> retrieveAll() {
        return mongoJobNode.findAll();
    }

    @Override
    public JobNode createJobNode(Project project, JobNodeDetails jobNodeDetails) {
        JobNode jobNode = new JobNode();
        jobNode.setProject(project);
        jobNode.setJobNodeDetails(jobNodeDetails);

        return mongoJobNode.save(jobNode);
    }

    @Override
    public JobNode updateJobNode(String id, JobNodeDetails jobNodeDetails) {
        
        JobNode jobNode = mongoJobNode.findById(id).get();
        jobNode.setJobNodeDetails(jobNodeDetails);
        return mongoJobNode.save(jobNode);
    }

    @Override
    public JobNode updateJobNodeFull(JobNode jobNode) {
        return mongoJobNode.save(jobNode);
    }

    @Override
    public void deleteJobNodeById(String id) {
        mongoJobNode.deleteById(id);
    }
}
