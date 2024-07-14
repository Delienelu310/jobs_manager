package com.ilumusecase.jobs_manager.controllers.ui_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class ProjectGraphController {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @GetMapping("/projects/{project_id}/graph")
    @JsonMapperRequest(type="graph", resource = "ProjectGraph")
    public Object retrieveProjectGraph(
        @ProjectId @PathVariable("project_id") String projectId
    ){
        return repositoryFactory.getProjectGraphRepository().retrieveProjectGraphByProjectId(projectId).orElseThrow();
    }

}
