package com.ilumusecase.jobs_manager.validation.resource_inheritance.handlers;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ilumusecase.jobs_manager.exceptions.ResourceNotFoundException;
import com.ilumusecase.jobs_manager.exceptions.WrongResourcesInheritanceInUrlException;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.ResourceIdsOperationsInstance;

public class JobNodeIdOperations implements ResourceIdsOperationsInstance{

    @Autowired
    private RepositoryFactory repositoryFactory;

    @Override
    public Class<? extends Annotation> getOperationsTarget() {
        return JobNodeId.class;
    }

    @Override
    public void validateResourceInheritance(Map<Class<? extends Annotation>, List<String>> ids) {
        
        for(String jobNodeId : ids.get(JobNodeId.class)){
            JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId).orElseThrow(() -> 
                new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));

            for(String projectId : ids.get(ProjectId.class)){
                if(!jobNode.getProject().getId().equals(projectId)) 
                    throw new WrongResourcesInheritanceInUrlException(Project.class.getSimpleName(), JobNode.class.getSimpleName());
            }
        }
        
    }
    
}
