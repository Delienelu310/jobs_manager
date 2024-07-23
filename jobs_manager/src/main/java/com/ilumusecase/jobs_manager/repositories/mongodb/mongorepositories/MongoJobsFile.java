package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;

public interface MongoJobsFile extends MongoRepository<JobsFile, String> {



    final String retrieveJobsFilesOfJobNodeQuery = "{'jobNode.id' : '?0', 'jobDetails.name': { $regex: '^?1', $options: 'i' }, " + 
            "$and: [" +
                "{ $or : [ " +
                    "{ 'extension': ?2 }, " +
                    "{ $expr: {   $eq: [ ?2, '' ] } }" +
                "] }, " + 

                "{ $or: [ " +
                    "{ 'allClasses': ?3 }, " +
                    "{ $expr: {   $eq: [ ?3, '' ] } }" +
                "] }, " +
                "{ $or: [ " +
                    "{ 'publisher.$id': ?4 }, " +
                    "{ $expr: {   $eq: [ ?4, '' ] } }" +
                "] }" +
            "]" +
        "}";


    @Query(retrieveJobsFilesOfJobNodeQuery)
    public List<JobsFile> retrieveJobsFilesOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String className, 
        String publisher,
        Pageable pageable
    );

    @Query(value = retrieveJobsFilesOfJobNodeQuery, count = true)
    public long countJobsFilesOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String className, 
        String publisher
    );


    @Query("{ 'jobNode.id' : ?0 }")
    public List<JobsFile> findByJobNodeId(String id);

    @Query("{ 'author.username' : ?0 }")
    public List<JobsFile> findByAuthorUsername(String username);
}
