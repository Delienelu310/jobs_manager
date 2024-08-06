package com.ilumusecase.jobs_manager.repositories.mongodb.authorization;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.ReplaceRootOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ilumusecase.jobs_manager.repositories.interfaces.authorization.AppUserRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.authorization.MongoAppUser;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.AppUserDetails;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;


public class MongoUserDetailsManager implements AppUserRepository{

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private MongoTemplate mongoTemplate;

    private MongoAppUser mongoAppUser;

    public MongoUserDetailsManager(MongoAppUser mongoAppUser, MongoTemplate mongoTemplate){
        this.mongoAppUser = mongoAppUser;
        this.mongoTemplate = mongoTemplate;
    }

    public boolean doesUserExist(String username){
        return mongoAppUser.existsById(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = mongoAppUser.findByUsername(username);

        if(appUser.isEmpty()) throw new UsernameNotFoundException(username);

        return appUser.get();
    }

    public AppUser findByUsername(String username){
        Optional<AppUser> appUser = mongoAppUser.findByUsername(username);

        if(appUser.isEmpty()) throw new UsernameNotFoundException(username);

        return appUser.get();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = findByUsername(authentication.getName());

        if(!user.getPassword().equals(passwordEncoder.encode(oldPassword))) throw new RuntimeException("Wrong password");

        user.setPassword(passwordEncoder.encode(newPassword));
        mongoAppUser.save(user);
    }

    @Override
    public void createUser(UserDetails user) {


        AppUser appUser = new AppUser();
        appUser.setNewState(user);

        AppUserDetails defaultAppUserDetails = new AppUserDetails();
        appUser.setAppUserDetails(defaultAppUserDetails);

        mongoAppUser.save(appUser);
    }


    @Override
    public void deleteUser(String username) {
        mongoAppUser.deleteByUsername(username);
    }

    @Override
    public void updateUser(UserDetails user) {
        AppUser appUser = mongoAppUser.findByUsername(user.getUsername()).orElseThrow(RuntimeException::new);

        appUser.setNewState(user);
        mongoAppUser.save(appUser);

    }

    public void updateAppUserDetails(String username, AppUserDetails appUserDetails){
        AppUser appUser = mongoAppUser.findByUsername(username).orElseThrow(RuntimeException::new);

        appUser.setAppUserDetails(appUserDetails);
        mongoAppUser.save(appUser);
    }

    @Override
    public boolean userExists(String username) {
       return mongoAppUser.findByUsername(username).isPresent();
    }

    @Override
    public List<AppUser> retrieveUsers(
        String query,
        String fullname,
        Integer pageSize,
        Integer pageNumber
    ) {
        Pageable pageable =  PageRequest.of(pageNumber, pageSize);

        return mongoAppUser.retrieveUsers(query, fullname, pageable);
    }

    @Override
    public long retrieveUsersCount(
        String query,
        String fullname
    ) {
        return mongoAppUser.retrieveUsersCount(query, fullname);
    }


    @Override
    public AppUser retrieveUserById(String id) {
        return mongoAppUser.findById(id).orElseThrow(RuntimeException::new);
    }

    @Override
    public void deleteUserById(String id) {
        mongoAppUser.deleteById(id);
    }

    @Override
    public AppUser saveAppUser(AppUser user) {
        return mongoAppUser.save(user);
    }

    private List<AggregationOperation> getPrivilegesRetrievementOperations(String id, String query,
        List privileges
    ){


        List<AggregationOperation> operations = new LinkedList<>();

        MatchOperation matchProject = Aggregation.match(
            Criteria.where("_id").is(id)
        );


        ProjectionOperation projectToConvertObjectToArray = Aggregation.project()
            .andExpression("objectToArray(privileges)").as("privilegesArray");

        UnwindOperation unwindPrivileges = Aggregation.unwind("privilegesArray");
        ProjectionOperation projectToReshape = Aggregation.project()
            .and("privilegesArray.k").as("username")
            .and("privilegesArray.v").as("privileges");

        Criteria criteria = Criteria.where("username").regex("^" + query);
        MatchOperation filterUsers = Aggregation.match(criteria);

        LookupOperation lookupPrivilegeList = Aggregation.lookup("privilegeList", "privileges.$id", "_id", "privilegeList");

        ProjectionOperation getList = Aggregation.project()
            .and("privilegeList.list").as("privilege")
            .and("username").as("username")
        ;

        UnwindOperation unwindSingleUserPrivileges = Aggregation.unwind("privilege");

        Criteria criteria2 = Criteria.where("username").regex("^");
        List<Criteria> privilegeCriterias = new LinkedList<>();
        if(!privileges.isEmpty()){
            for(Object privilege : privileges){
                privilegeCriterias.add(Criteria.where("privilege").is(privilege.toString()));
            }
        }
        if(!privilegeCriterias.isEmpty()) criteria2.orOperator(privilegeCriterias);

        MatchOperation filterUsersByPrivilege = Aggregation.match(criteria2);


        LookupOperation lookupUsers = Aggregation.lookup("users", "username", "_id", "appUser");

        UnwindOperation unwindAppUser = Aggregation.unwind("appUser");
        ReplaceRootOperation replaceRootWithAppUser = Aggregation.replaceRoot("appUser");


        Collections.addAll(operations, 
            matchProject,
            projectToConvertObjectToArray,
            unwindPrivileges,
            projectToReshape,
            filterUsers,
            lookupPrivilegeList,
            getList,
            unwindSingleUserPrivileges,
            filterUsersByPrivilege,
            lookupUsers,
            unwindAppUser,
            replaceRootWithAppUser
        );

        return operations;
    }
 

    @Override
    public List<AppUser> retrieveProjectPrivileges(String projectId, String query,
        List<ProjectPrivilege> projectPrivileges, Integer pageSize,
        Integer pageNumber
    ) {

        List<AggregationOperation> operations = getPrivilegesRetrievementOperations(projectId, query, projectPrivileges);
    
        operations.add(Aggregation.skip(pageSize * pageNumber));
        operations.add(Aggregation.limit(pageSize));

        Aggregation aggregation = Aggregation.newAggregation(operations );

        AggregationResults<AppUser> results = mongoTemplate.aggregate(aggregation, "project", AppUser.class);

        return results.getMappedResults();
    }

    @Override
    public long retrieveProjectPrivilegesCount(String projectId, String query, 
        List<ProjectPrivilege> projectPrivileges
    ){

        List<AggregationOperation> operations = getPrivilegesRetrievementOperations(projectId, query, projectPrivileges);
    
        operations.add(Aggregation.count().as("count"));

        Aggregation aggregation = Aggregation.newAggregation(operations );

        AggregationResults<Map> countResults = mongoTemplate.aggregate(aggregation, "project", Map.class);

        long totalCount = 0;
        if (countResults.getUniqueMappedResult() != null) {
            totalCount = ((Number) countResults.getUniqueMappedResult().get("count")).intValue();
        }

        return totalCount;

        
    }



    @Override
    public List<AppUser> retrieveJobNodePrivileges(String jobNodeId, String query,
        List<JobNodePrivilege> jobNodePrivileges, Integer pageSize, Integer pageNumber
    ) {
        List<AggregationOperation> operations = getPrivilegesRetrievementOperations(jobNodeId, query, jobNodePrivileges);
    
        operations.add(Aggregation.skip(pageSize * pageNumber));
        operations.add(Aggregation.limit(pageSize));

        Aggregation aggregation = Aggregation.newAggregation(operations );

        AggregationResults<AppUser> results = mongoTemplate.aggregate(aggregation, "jobNode", AppUser.class);

        return results.getMappedResults();
        
    }

    @Override
    public long retrieveJobNodePrivilegesCount(String jobNodeId, String query,
        List<JobNodePrivilege> jobNodePrivileges
    ) {
        List<AggregationOperation> operations = getPrivilegesRetrievementOperations(jobNodeId, query, jobNodePrivileges);
    
        operations.add(Aggregation.count().as("count"));

        Aggregation aggregation = Aggregation.newAggregation(operations );

        AggregationResults<Map> countResults = mongoTemplate.aggregate(aggregation, "jobNode", Map.class);

        long totalCount = 0;
        if (countResults.getUniqueMappedResult() != null) {
            totalCount = ((Number) countResults.getUniqueMappedResult().get("count")).intValue();
        }

        return totalCount;
    }
 

}
