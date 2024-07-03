package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;

@Repository
public interface MongoJobNode extends MongoRepository<JobNode, String>{
    @Query("{ 'project.id' : ?0 }")
    List<JobNode> retrieveByProjectId(String projectId);
}
