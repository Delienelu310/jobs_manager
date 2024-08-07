package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.ilum;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Repository
public interface MongoIlumGroup extends MongoRepository<IlumGroup, String>{
    public IlumGroup findByIlumId(String id);
}