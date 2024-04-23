package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.ProjectRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoProject;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.resources.ProjectDetails;

@Repository
public class ProjectMongoRepository implements ProjectRepository {

    private MongoProject mongoProject;

    @Override
    public List<Project> retrieveAllProjects() {
        return mongoProject.findAll();
    }

    @Override
    public Project retrieveProjectById(Long id) {
        return mongoProject.findById(id).get();
    }

    @Override
    public Project createProject(ProjectDetails projectDetails) {
        Project project = new Project();
        project.setProjectDetails(projectDetails);

        return mongoProject.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        mongoProject.deleteById(id);
    }
    
}
