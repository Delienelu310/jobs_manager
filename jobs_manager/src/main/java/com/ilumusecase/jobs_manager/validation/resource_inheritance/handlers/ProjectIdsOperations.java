package com.ilumusecase.jobs_manager.validation.resource_inheritance.handlers;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.ResourceIdsOperationsInstance;

@Component
public class ProjectIdsOperations implements ResourceIdsOperationsInstance {

    @Override
    public Class<? extends Annotation> getOperationsTarget() {
        return ProjectId.class;
    }

    @Override
    public void validateResourceInheritance(Map< Class<? extends Annotation> , List<String>> ids) {
        return;
    }
    
}
