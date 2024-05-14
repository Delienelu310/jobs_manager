package com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ilumusecase.jobs_manager.resources.ProjectPrivilege;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizeProjectRoles {
    public ProjectPrivilege[] roles() default {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR};
}
