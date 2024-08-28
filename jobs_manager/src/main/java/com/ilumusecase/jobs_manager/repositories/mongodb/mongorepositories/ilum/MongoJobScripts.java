package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.ilum;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

@Repository
public interface MongoJobScripts extends MongoRepository<JobScript, String> {
    
    final String retrieveJobsFilesOfJobNodeQuery = "{'jobNode.id' : '?0', 'jobScriptDetails.name': { $regex: '^?1', $options: 'i' }, " + 
            "$and: [" +
                "{ $or : [ " +
                    "{ 'extension': ?2 }, " +
                    "{ $expr: {   $eq: [ ?2, '' ] } }" +
                "] }, " + 
                "{ $or: [ " +
                    "{ 'author.$id': ?3 }, " +
                    "{ $expr: {   $eq: [ ?3, '' ] } }" +
                "] }" +
            "]" +
        "}";


    @Query(retrieveJobsFilesOfJobNodeQuery)
    public List<JobScript> retrieveJobScriptsOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String publisher,
        Pageable pageable
    );

    @Query(value = retrieveJobsFilesOfJobNodeQuery, count = true)
    public long countJobScriptsOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String publisher
    );



    @Query("{ 'jobsFiles.id': ?0 }")
    public List<JobScript> retrieveByJobsFileId(String jobsFileId);

    @Query("{ 'jobNode.id': ?0}")
    public List<JobScript> retrieveByJobNodeId(String jobNodeId);

    public void deleteByJobNodeId(String jobNodeId);
}
