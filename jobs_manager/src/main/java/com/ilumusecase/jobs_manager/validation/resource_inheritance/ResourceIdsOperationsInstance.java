package com.ilumusecase.jobs_manager.validation.resource_inheritance;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public interface ResourceIdsOperationsInstance {
    
    public Class<? extends Annotation> getOperationsTarget();

    public void validateResourceInheritance(Map< Class<? extends Annotation> , List<String>> ids);
}
