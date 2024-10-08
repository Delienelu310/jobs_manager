package com.ilumusecase.jobs_manager.repositories.mongodb.abstraction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.exceptions.ResourceNotFoundException;
import com.ilumusecase.jobs_manager.repositories.interfaces.abstraction.ProjectRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.abstraction.MongoProject;
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
        return mongoProject.findById(id).orElseThrow(() -> new ResourceNotFoundException(Project.class.getSimpleName(), id));
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

    @Override
    public List<Project> retrieveProjectsFiltered(int pageSize, int pageNumber, String query, String username, String admin) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        if(admin == null || admin.equals("")){
            return mongoProject.retrieveProjectsFiltered(query, username, pageable);
        }else{
            return mongoProject.retrieveProjectsFilteredAdvanced(query, username, admin, pageable);
        }
    }

    @Override
    public long countProjectsFiltered(String query, String username, String admin){
        if(admin == null || admin.equals("")){
            return mongoProject.countProjectsFiltered(query, username);
        }else{
            return mongoProject.countProjectsFilteredAdvanced(query, username, admin);
        }
    }
    
}
