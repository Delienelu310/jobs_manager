package com.ilumusecase.jobs_manager.repositories.mongodb.ilum;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers.JobResultsController.IlumGroupData;
import com.ilumusecase.jobs_manager.repositories.interfaces.ilum.JobResultRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.ilum.MongoJobResult;
import com.ilumusecase.jobs_manager.resources.ilum.JobResult;

@Repository
public class JobResultMongoRepository  implements JobResultRepository{

    @Autowired
    private MongoJobResult mongoJobResult;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void clear(){
        mongoJobResult.deleteAll();
    }

    @Override
    public void deleteJobResultById(String id) {
        mongoJobResult.deleteById(id);
    }

    @Override
    public String updateJobResultFull(JobResult jobResult) {
        return mongoJobResult.save(jobResult).getId();
    }

    



    @Override
    public List<JobResult> retrieveAll() {
        return mongoJobResult.findAll();
    }


    private List<AggregationOperation> getJobResultsRetrievementOperations(String jobNodeId, String ilumGroupId, String targetNameQuery,
        boolean includeSuccessfull, boolean includeJobErrors, boolean includeTesterErrors, String targetAuthor,
        String targetClass, String targetId, String testerNameQuery, String testerAuthor, String testerClass,
        String testerId, Long from, Long to
    ){
        List<AggregationOperation> operations = new LinkedList<>();

        //1. match by jobNodeId, from, to, includes
        Criteria directMatchCriteria = Criteria.where("jobNode.$id").is(new ObjectId(jobNodeId))
            .and("startTime").gte(from == null ? 0 : from)
            .and("endTime").lte(to == null ? Long.MAX_VALUE : to)
        ;

        Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);
        logger.info(Boolean.toString(includeJobErrors) + " " + Boolean.toString(includeTesterErrors));

        if(!includeSuccessfull) directMatchCriteria = directMatchCriteria.and("jobResultDetails.errorMessage").ne(null);
        if(!includeJobErrors) directMatchCriteria = directMatchCriteria.and("tester").ne(null);
        if(!includeTesterErrors) directMatchCriteria = directMatchCriteria.orOperator(
            Criteria.where("jobResultDetails.errorMessage").is(null), 
            Criteria.where("tester").is(null)
        );



        //2. match by ilumGropuId, testerId and targetId

        if(!ilumGroupId.equals("")) directMatchCriteria = directMatchCriteria.and("ilumGroupId").is(ilumGroupId);
        if(!targetId.equals("")) directMatchCriteria = directMatchCriteria.and("target.$id").is(new ObjectId(targetId));
        if(!testerId.equals("") && !includeJobErrors) directMatchCriteria = directMatchCriteria.and("tester.$id").is(new ObjectId(testerId));

        MatchOperation mainMatch = Aggregation.match(directMatchCriteria);

        operations.add(mainMatch);

        //3. lookup tester, if required
        //4. match tester details
        if(testerId.equals("") && !includeJobErrors){
            LookupOperation lookupTester = Aggregation.lookup("jobScript", "tester.$id", "_id", "testerDocument");

            operations.add(lookupTester);
            

            Criteria matchTesterCriteria = Criteria.where("testerDocument.jobScriptDetails.name").regex("^" + testerNameQuery);
            if(!testerAuthor.equals("")) matchTesterCriteria = matchTesterCriteria.and("testerDocument.author.$id").is(testerAuthor);
            if(!testerClass.equals("")) matchTesterCriteria = matchTesterCriteria.and("testerDocument.fullClassName").is(testerClass);

            MatchOperation matchTester = Aggregation.match(matchTesterCriteria);

            operations.add(matchTester);
        }
       

        //5. lookup target, if required
        //6. match target details
        if(targetId.equals("")){
            LookupOperation lookupTarget = Aggregation.lookup("jobScript", "target.$id", "_id", "targetDocument");

            operations.add(lookupTarget);

            Criteria matchTargetCriteria = Criteria.where("targetDocument.jobScriptDetails.name").regex("^" + targetNameQuery);
            if(!targetAuthor.equals("")) matchTargetCriteria = matchTargetCriteria.and("targetDocument.author.$id").is(targetAuthor);
            if(!targetClass.equals("")) matchTargetCriteria = matchTargetCriteria.and("targetDocument.fullClassName").is(targetClass);

            MatchOperation matchTarget = Aggregation.match(matchTargetCriteria);

            operations.add(matchTarget);
        }


        return operations;
    }

    @Override
    public List<JobResult> retrieveJobResults(String jobNodeId, String ilumGroupId, String targetNameQuery,
        boolean includeSuccessfull, boolean includeJobErrors, boolean includeTesterErrors, String targetAuthor,
        String targetClass, String targetId, String testerNameQuery, String testerAuthor, String testerClass,
        String testerId, Long from, Long to, String sortMetric, Integer pageSize, Integer pageNumber
    ) {
        List<AggregationOperation> operations = getJobResultsRetrievementOperations(jobNodeId, ilumGroupId, targetNameQuery, 
            includeSuccessfull, includeJobErrors, includeTesterErrors, 
            targetAuthor, targetClass, targetId, 
            testerNameQuery, testerAuthor, testerClass, testerId, 
            from, to
        );

        if(!sortMetric.equals("")) operations.add(Aggregation.sort(Sort.by("jobResultDetails.metrics." +sortMetric).descending()));
        operations.add(Aggregation.skip(pageSize * pageNumber));
        operations.add(Aggregation.limit(pageSize));

        Aggregation aggregation = Aggregation.newAggregation(operations );

        AggregationResults<JobResult> results = mongoTemplate.aggregate(aggregation, "jobResult", JobResult.class);

        return results.getMappedResults();  

    }

    @Override
    public Long retrieveJobResultsCount(String jobNodeId, String ilumGroupId, String targetNameQuery,
        boolean includeSuccessfull, boolean includeJobErrors, boolean includeTesterErrors, String targetAuthor,
        String targetClass, String targetId, String testerNameQuery, String testerAuthor, String testerClass,
        String testerId, Long from, Long to
    ) {
        List<AggregationOperation> operations = getJobResultsRetrievementOperations(jobNodeId, ilumGroupId, targetNameQuery, 
            includeSuccessfull, includeJobErrors, includeTesterErrors, 
            targetAuthor, targetClass, targetId, 
            testerNameQuery, testerAuthor, testerClass, testerId, 
            from, to
        );

        operations.add(Aggregation.count().as("count"));

        Aggregation aggregation = Aggregation.newAggregation(operations );

        AggregationResults<Map> countResults = mongoTemplate.aggregate(aggregation, "jobResult", Map.class);

        long totalCount = 0;
        if (countResults.getUniqueMappedResult() != null) {
            totalCount = ((Number) countResults.getUniqueMappedResult().get("count")).intValue();
        }

        return totalCount;
    }

    @Override
    public Optional<JobResult> retrieveById(String id) {
        return mongoJobResult.findById(id);
    }

    @Override
    public Optional<JobResult> retrieveByIlumId(String ilumId) {
        return mongoJobResult.findByIlumId(ilumId);
    }


    private List<AggregationOperation> getIlumGroupsRetrievementOperations(
        String jobNodeId, String query, Long from, Long to
    ){
        List<AggregationOperation> operations = new LinkedList<>();

        
        MatchOperation matchJobNodeId = Aggregation.match(Criteria.where("jobNode.$id").is(new ObjectId(jobNodeId)));

        ProjectionOperation projectionOperation = Aggregation.project("ilumGroupId", "ilumGroupDetails");

        GroupOperation groupOperation = Aggregation.group("ilumGroupId")
            .first("ilumGroupDetails").as("ilumGroupDetails")
            .last("ilumGroupId").as("ilumGroupId");

        MatchOperation queryMatch = Aggregation.match(
            Criteria.where("ilumGroupDetails.name").regex("^" + query)
                .and("ilumGroupDetails.startTime")
                    .lte(to == null ? Long.MAX_VALUE : to)
                    .gte(from == null ? 0 : from)
        );

        Collections.addAll(operations, matchJobNodeId, projectionOperation, groupOperation, queryMatch);

        return operations;
    }

    @Override
    public List<IlumGroupData> retrieveIlumGroupsOfJobResults(String jobNodeId, String query, Long from, Long to, Integer pageSize,
        Integer pageNumber
    ) {
        List<AggregationOperation> operations = getIlumGroupsRetrievementOperations(jobNodeId, query, from, to);
   

        operations.add(Aggregation.sort(Sort.by("ilumGroupDetails.startTime").descending()));
        operations.add(Aggregation.skip(pageSize * pageNumber));
        operations.add(Aggregation.limit(pageSize));

        Aggregation aggregation = Aggregation.newAggregation(operations );

        AggregationResults<IlumGroupData> results = mongoTemplate.aggregate(aggregation, "jobResult", IlumGroupData.class);

        return results.getMappedResults();  
    }

    @Override
    public Long retrieveIlumGroupsOfJobResultsCount(String jobNodeId, String query, Long from, Long to) {
        List<AggregationOperation> operations = getIlumGroupsRetrievementOperations(jobNodeId, query, from, to);
        
        operations.add(Aggregation.count().as("count"));

        Aggregation aggregation = Aggregation.newAggregation(operations );

        AggregationResults<Map> countResults = mongoTemplate.aggregate(aggregation, "jobResult", Map.class);

        long totalCount = 0;
        if (countResults.getUniqueMappedResult() != null) {
            totalCount = ((Number) countResults.getUniqueMappedResult().get("count")).intValue();
        }

        return totalCount;
    }
    




}
