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
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.ResourceIdsOperationsInstance;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.annotations.JobsFileId;

@Component
public class JobsFileIdOperations implements ResourceIdsOperationsInstance {

    @Autowired 
    private RepositoryFactory repositoryFactory;

    @Override
    public Class<? extends Annotation> getOperationsTarget() {
        return JobsFileId.class;
    }

    @Override
    public void validateResourceInheritance(Map<Class<? extends Annotation>, List<String>> ids) {

        for(String jobsFileId : ids.getOrDefault(getOperationsTarget(), new ArrayList<>())){
            JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId)
                .orElseThrow(() -> new ResourceNotFoundException(JobsFile.class.getSimpleName(), jobsFileId));

            for(String jobNodeId : ids.getOrDefault(JobNodeId.class, new ArrayList<>())){
                if(!jobsFile.getJobNode().getId().equals(jobNodeId))
                    throw new WrongResourcesInheritanceInUrlException(JobNode.class.getSimpleName(), JobsFile.class.getSimpleName());
            }

            for(String projectId : ids.getOrDefault(ProjectId.class, new ArrayList<>())){
                if(!jobsFile.getProject().getId().equals(projectId))
                    throw new WrongResourcesInheritanceInUrlException(Project.class.getSimpleName(), JobsFile.class.getSimpleName());
            }
        }
    }
    
}
