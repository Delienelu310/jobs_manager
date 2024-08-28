package com.ilumusecase.jobs_manager.repositories.mongodb.abstraction;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.exceptions.ResourceNotFoundException;
import com.ilumusecase.jobs_manager.repositories.interfaces.abstraction.JobNodesRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.authorization.JobNodePrivilegeListMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.ilum.JobMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.ilum.JobResultMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.ilum.JobScriptsMongoRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.ilum.JobsFileMongo;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.abstraction.MongoJobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNodeDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

@Repository
public class JobNodesMongoRepository implements JobNodesRepository{

    @Autowired
    private MongoJobNode mongoJobNode;
    
    @Autowired
    private JobNodePrivilegeListMongoRepository jobNodePrivilegeListMongoRepository; 
    @Autowired
    private JobResultMongoRepository jobResultMongoRepository;
    @Autowired 
    private JobsFileMongo jobsFileMongo;
    @Autowired
    private JobScriptsMongoRepository jobScriptsMongoRepository;
    @Autowired
    private JobMongoRepository jobMongoRepository;

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
        //delete job node privilege list
        JobNode jobNode = retrieveById(id).orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), id));
        jobNode.getPrivileges().values().forEach(privilegeList -> jobNodePrivilegeListMongoRepository.delete(privilegeList.getId()));

        //delete job results
        jobResultMongoRepository.clearAll(id, "", "", "", true, true, true);
        
        //delete job entities
        jobMongoRepository.deleteByJobNodeId(id);


        //delete job scripts
        jobScriptsMongoRepository.deleteByJobNodeId(id);

        //delete job files


        

        mongoJobNode.deleteById(id);
    }
}
