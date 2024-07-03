package com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;


@Component
public class ProjectAuthHandler implements AnnotationHandlerInterface{

    @Autowired
    private RepositoryFactory repositoryFactory;


    @Override
    public boolean authorize(JoinPoint joinPoint, Method method, Annotation annotation, Authentication authentication) {
    
        AuthorizeProjectRoles authorizeProjectRoles = (AuthorizeProjectRoles)annotation;
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());

        //find projectId - argument with ProjectId annotation
        Optional<String> projectId = Optional.empty();
        int index = 0;
        for(Parameter parameter : method.getParameters()){
            if(parameter.isAnnotationPresent(ProjectId.class)){
                projectId = Optional.of((String)joinPoint.getArgs()[index]);
                break;
            }
            index++;
        }
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId.orElseThrow(RuntimeException::new));

        
        return project.getPrivileges().get(appUser.getUsername()).getList().stream().anyMatch(role -> {
            for(ProjectPrivilege projectPrivilege : authorizeProjectRoles.roles()){
                if(projectPrivilege == role) return true;
            }
            return false;
        });

    }
    
}
