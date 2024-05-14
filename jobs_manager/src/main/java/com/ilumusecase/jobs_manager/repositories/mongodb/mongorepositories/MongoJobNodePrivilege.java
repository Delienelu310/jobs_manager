package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.PrivilegeList;

@Repository
public interface MongoJobNodePrivilege extends MongoRepository<PrivilegeList<JobNodePrivilege>, String>{

}
