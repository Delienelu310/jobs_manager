package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.ProjectGraphRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoProjectrGraph;
import com.ilumusecase.jobs_manager.resources.ui.ProjectGraph;

@Repository
public class ProjectGraphMongoRepository implements ProjectGraphRepository{

    @Autowired
    private MongoProjectrGraph mongoProjectrGraph;

    @Override
    public Optional<ProjectGraph> retrieveProjectGraphByProjectId(String projectId) {
        return mongoProjectrGraph.findByProject_Id(projectId);
    }

    @Override
    public ProjectGraph updateProjectGraph(ProjectGraph projectGraph) {
        return mongoProjectrGraph.save(projectGraph);
    }
    
}
