package com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ilumusecase.jobs_manager.resources.JobNodePrivilege;

//requires 2 arguments in target method: projectId and jobNodeId
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizeJobRoles {
    public JobNodePrivilege[] roles() default {};
}
