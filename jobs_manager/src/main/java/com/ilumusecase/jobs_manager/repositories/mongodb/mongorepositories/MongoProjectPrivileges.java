package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.PrivilegeList;
import com.ilumusecase.jobs_manager.resources.ProjectPrivilege;

@Repository
public interface MongoProjectPrivileges extends MongoRepository<PrivilegeList<ProjectPrivilege>, String>{

    
}
