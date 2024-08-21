package com.ilumusecase.jobs_manager.validation.resource_inheritance.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import com.ilumusecase.jobs_manager.exceptions.WrongResourcesInheritanceInUrlException;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.ilum.JobResult;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.ResourceIdsOperationsInstance;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.annotations.JobResultId;

public class JobResultIdsOperations implements ResourceIdsOperationsInstance {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @Override
    public Class<? extends Annotation> getOperationsTarget() {
        return JobResultId.class;
    }

    @Override
    public void validateResourceInheritance(Map<Class<? extends Annotation>, List<String>> ids) {
         for(String jobResultId : ids.getOrDefault(getOperationsTarget(), new ArrayList<>())){
            JobResult jobResult = repositoryFactory.getJobResultRepository().retrieveById(jobResultId)
                .orElseThrow(() -> new ResourceNotFoundException(JobResult.class.getSimpleName(), jobResultId));

            for(String jobNodeId : ids.getOrDefault(JobNodeId.class, new ArrayList<>())){
                if(!jobResult.getJobNode().getId().equals(jobNodeId))
                    throw new WrongResourcesInheritanceInUrlException(JobNode.class.getSimpleName(), JobResult.class.getSimpleName());
            }

            for(String projectId : ids.getOrDefault(ProjectId.class, new ArrayList<>())){
                if(!jobResult.getProject().getId().equals(projectId))
                    throw new WrongResourcesInheritanceInUrlException(Project.class.getSimpleName(), JobResult.class.getSimpleName());
            }
        }
    }
    
}
