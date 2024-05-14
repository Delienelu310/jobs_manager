package com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ilumusecase.jobs_manager.security.Roles;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizeRoles {
    public Roles[] roles() default {};
}
