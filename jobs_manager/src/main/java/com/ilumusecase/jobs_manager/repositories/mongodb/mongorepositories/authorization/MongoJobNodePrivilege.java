package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.authorization;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;

@Repository
public interface MongoJobNodePrivilege extends MongoRepository<PrivilegeList<JobNodePrivilege>, String>{

}
