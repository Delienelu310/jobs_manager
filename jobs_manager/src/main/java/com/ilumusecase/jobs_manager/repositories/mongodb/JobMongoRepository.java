package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.ReplaceRootOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.JobRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoJobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

@Repository
public class JobMongoRepository implements JobRepository{
    
    @Autowired
    private MongoJobEntity mongoJobEntity;

    @Autowired
    private MongoTemplate mongoTemplate;

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

    @Override
    public List<JobEntity> retrieveQueue(
        String jobNodeId, String queueType, String jobEntityName, String author,
            Integer pageSize, Integer pageNumber
    ){

        MatchOperation matchJobNode = Aggregation.match(
            Criteria.where("_id").is(jobNodeId)
        );

        LookupOperation lookupJobEntities = Aggregation.lookup(
            "jobEntity", 
            queueType + ".$id",   
            "_id",      
            "jobEntity"    
        );
        
        UnwindOperation unwindQueue = Aggregation.unwind("jobEntity");
        ProjectionOperation projectQueue = Aggregation.project()
            .and("jobEntity").as("jobEntity");

        
        Criteria criteria = Criteria.where("jobEntityDetails.name").regex("^" + jobEntityName);
        if(!author.equals("")){
            criteria = criteria.and("author.$id").is(author);
        }
        MatchOperation matchJobEntities = Aggregation.match(criteria);

        ReplaceRootOperation replaceRootWithJobEntity = Aggregation.replaceRoot("jobEntity");


        Aggregation aggregation = Aggregation.newAggregation(
            matchJobNode,
            lookupJobEntities,
            unwindQueue, 
            projectQueue,
            replaceRootWithJobEntity,
            matchJobEntities
        );

        AggregationResults<JobEntity> results = mongoTemplate.aggregate(aggregation, "jobNode", JobEntity.class);

        return results.getMappedResults();
    }

    
}
