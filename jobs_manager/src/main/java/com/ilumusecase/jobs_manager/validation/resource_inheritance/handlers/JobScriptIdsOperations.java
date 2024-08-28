package com.ilumusecase.jobs_manager.validation.resource_inheritance.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.exceptions.ResourceNotFoundException;
import com.ilumusecase.jobs_manager.exceptions.WrongResourcesInheritanceInUrlException;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.ResourceIdsOperationsInstance;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.annotations.JobScriptId;

@Component
public class JobScriptIdsOperations implements ResourceIdsOperationsInstance {


    @Autowired
    private RepositoryFactory repositoryFactory;

    @Override
    public Class<? extends Annotation> getOperationsTarget() {
        return JobScriptId.class;
    }

    @Override
    public void validateResourceInheritance(Map<Class<? extends Annotation>, List<String>> ids) {

        for(String jobScriptId : ids.getOrDefault(getOperationsTarget(), new ArrayList<>())){
            JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId)
                .orElseThrow(() -> new ResourceNotFoundException(JobScript.class.getSimpleName(), jobScriptId));

            for(String jobNodeId : ids.getOrDefault(JobNodeId.class, new ArrayList<>())){
                if(!jobScript.getJobNode().getId().equals(jobNodeId))
                    throw new WrongResourcesInheritanceInUrlException(JobNode.class.getSimpleName(), JobScript.class.getSimpleName());
            }

            for(String projectId : ids.getOrDefault(ProjectId.class, new ArrayList<>())){
                if(!jobScript.getProject().getId().equals(projectId))
                    throw new WrongResourcesInheritanceInUrlException(Project.class.getSimpleName(), JobScript.class.getSimpleName());
            }
        }
    }
    
}
