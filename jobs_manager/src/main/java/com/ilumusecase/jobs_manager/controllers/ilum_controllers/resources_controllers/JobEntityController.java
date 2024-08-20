package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.exceptions.WrongResourcesInheritanceInUrlException;
import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class JobEntityController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_entities/{job_entity_id}")
    @JsonMapperRequest(type="simple", resource = "JobEntity")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public Object getJobEntityById(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_entity_id") String jobEntityId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));

        JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobEntityId);
        if(!projectId.equals(jobNode.getProject().getId())) throw new WrongResourcesInheritanceInUrlException(Project.class.getSimpleName(), JobNode.class.getSimpleName());
        if(!jobNodeId.equals(jobEntity.getJobNode().getId())) throw new WrongResourcesInheritanceInUrlException(JobNode.class.getSimpleName(), JobEntity.class.getSimpleName());


        return jobEntity;

    }


}
