package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.ProjectRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoProject;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.abstraction.ProjectDetails;

@Repository
public class ProjectMongoRepository implements ProjectRepository {

    @Autowired
    private MongoProject mongoProject;

    @Override
    public List<Project> retrieveAllProjects() {
        return mongoProject.findAll();
    }

    @Override
    public Project retrieveProjectById(String id) {
        return mongoProject.findById(id).get();
    }

    @Override
    public Project createProject(ProjectDetails projectDetails) {
        Project project = new Project();
        project.setProjectDetails(projectDetails);

        return mongoProject.save(project);
    }

    @Override
    public void deleteProject(String id) {
        mongoProject.deleteById(id);
    }

    @Override
    public Project updateProject(String id, ProjectDetails projectDetails) {

        Project project = mongoProject.findById(id).get();

        project.setProjectDetails(projectDetails);

        return mongoProject.save(project);
    }

    @Override
    public Project updateProjectFull(Project project) {
        return mongoProject.save(project);
    }
    
}
