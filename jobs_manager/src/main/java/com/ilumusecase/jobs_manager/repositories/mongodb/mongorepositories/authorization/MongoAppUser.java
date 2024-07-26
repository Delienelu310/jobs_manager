package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.authorization;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.authorities.AppUser;

@Repository
public interface MongoAppUser extends MongoRepository<AppUser, String>{

    @Query("{ '_id' : { $regex : '^?0', $options : 'i'}, 'appUserDetails.fullname' : {$regex : '^?1', $options : 'i'}}")
    List<AppUser> retrieveUsers(String query, String fullname, Pageable pageable);
    @Query(value="{_id : { $regex : '^?0'}, appUserDetails.fullName : {$regex : '^?1'}}", count=true)
    long retrieveUsersCount(String query, String fullname);

    Optional<AppUser> findByUsername(String username);
    void deleteByUsername(String username);
}
