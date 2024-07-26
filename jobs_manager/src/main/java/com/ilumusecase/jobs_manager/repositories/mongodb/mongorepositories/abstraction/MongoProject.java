package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.abstraction;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.abstraction.Project;

@Repository
public interface MongoProject extends MongoRepository<Project, String>{
    @Query("{'projectDetails.name': { $regex: '^?0', $options: 'i' },  'privileges.?1': { $exists: true },  'admin': { $eq: '?2' } }")
    public List<Project> retrieveProjectsFilteredAdvanced(String query, String username, String admin, Pageable pageable);

    @Query("{'projectDetails.name': { $regex: '^?0', $options: 'i' }, 'privileges.?1': { $exists: true }}")
    public List<Project> retrieveProjectsFiltered(String query, String username, Pageable pageable);
    

    @Query(value = "{'projectDetails.name': { $regex: '^?0', $options: 'i' },  'privileges.?1': { $exists: true },  'admin': { $eq: '?2' } }", count=true)
    public long countProjectsFilteredAdvanced(String query, String username, String admin );

    @Query(value = "{'projectDetails.name': { $regex: '^?0', $options: 'i' }, 'privileges.?1': { $exists: true }}", count = true)
    public long countProjectsFiltered(String query, String username);
    
}
