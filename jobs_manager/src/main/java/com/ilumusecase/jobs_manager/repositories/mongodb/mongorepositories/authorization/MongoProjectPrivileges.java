package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.authorization;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;

@Repository
public interface MongoProjectPrivileges extends MongoRepository<PrivilegeList<ProjectPrivilege>, String>{

    
}
