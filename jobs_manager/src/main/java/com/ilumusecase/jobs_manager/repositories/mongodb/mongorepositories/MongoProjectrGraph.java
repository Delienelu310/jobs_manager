package com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.resources.ui.ProjectGraph;

@Repository
public interface MongoProjectrGraph extends MongoRepository<ProjectGraph, String>{
    Optional<ProjectGraph> findByProject_Id(String projectId);
}
