package com.ilumusecase.jobs_manager.repositories.mongodb.abstraction;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.abstraction.JobNodesRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.abstraction.MongoJobNode;
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
    public Optional<JobNode> retrieveById(String id) {
        return mongoJobNode.findById(id);
    }

    @Override
    public List<JobNode> retrieveAll() {
        return mongoJobNode.findAll();
    }

    @Override
    public String createJobNode(Project project, JobNodeDetails jobNodeDetails) {
        JobNode jobNode = new JobNode();
        jobNode.setProject(project);
        jobNode.setJobNodeDetails(jobNodeDetails);

        return mongoJobNode.save(jobNode).getId();
    }

    @Override
    public void updateJobNode(String id, JobNodeDetails jobNodeDetails) {
        
        JobNode jobNode = mongoJobNode.findById(id).get();
        jobNode.setJobNodeDetails(jobNodeDetails);
        mongoJobNode.save(jobNode);
    }

    @Override
    public void updateJobNodeFull(JobNode jobNode) {
        mongoJobNode.save(jobNode);
    }

    @Override
    public void deleteJobNodeById(String id) {
        mongoJobNode.deleteById(id);
    }
}
