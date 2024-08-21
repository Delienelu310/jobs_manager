package com.ilumusecase.jobs_manager.validation.resource_inheritance.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.exceptions.WrongResourcesInheritanceInUrlException;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.ResourceIdsOperationsInstance;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.annotations.JobEntityId;

@Component
public class JobEntityIdOperations implements ResourceIdsOperationsInstance {


    @Autowired
    private RepositoryFactory repositoryFactory;

    @Override
    public Class<? extends Annotation> getOperationsTarget() {
        return JobEntityId.class;
    }

    @Override
    public void validateResourceInheritance(Map<Class<? extends Annotation>, List<String>> ids) {
        for(String jobEntityId : ids.getOrDefault(getOperationsTarget(), new ArrayList<>())){
            JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobEntityId)
                .orElseThrow(() -> new ResourceNotFoundException(JobEntity.class.getSimpleName(), jobEntityId));

            for(String jobNodeId : ids.getOrDefault(JobNodeId.class, new ArrayList<>())){
                if(!jobEntity.getJobNode().getId().equals(jobNodeId))
                    throw new WrongResourcesInheritanceInUrlException(JobNode.class.getSimpleName(), JobEntity.class.getSimpleName());
            }

            for(String projectId : ids.getOrDefault(ProjectId.class, new ArrayList<>())){
                if(!jobEntity.getProject().getId().equals(projectId))
                    throw new WrongResourcesInheritanceInUrlException(Project.class.getSimpleName(), JobEntity.class.getSimpleName());
            }
        }
    }
    
}
