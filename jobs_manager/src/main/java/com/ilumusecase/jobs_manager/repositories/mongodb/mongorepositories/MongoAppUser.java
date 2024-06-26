package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.AppUser;

@Repository
public interface MongoAppUser extends MongoRepository<AppUser, String>{
    Optional<AppUser> findByUsername(String username);
    void deleteByUsername(String username);
}
