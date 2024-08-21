package com.ilumusecase.jobs_manager.validation.resource_inheritance;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResourceInheritanceValidationAspect {

    @Autowired
    private ResourceIdsOperationsFactory resourceIdsOperationsFactory;


    @Pointcut("execution(* com.ilumusecase.jobs_manager.controllers..*(..))")
    public void allControllersPointcut(){}

    @Before("allControllersPointcut()")
    public void validateResourceInheritance(JoinPoint joinPoint){

        resourceIdsOperationsFactory.applyValidations(resourceIdsOperationsFactory.extractIds(joinPoint));
    }

}
