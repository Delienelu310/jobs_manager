package com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.springframework.security.core.Authentication;

public interface AnnotationHandlerInterface {
    
    public boolean authorize(JoinPoint joinPoint, Method method, Annotation annotation, Authentication authentication);
}
