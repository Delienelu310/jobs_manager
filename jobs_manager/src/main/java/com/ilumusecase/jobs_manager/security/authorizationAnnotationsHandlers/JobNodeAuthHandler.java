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
import com.ilumusecase.jobs_manager.resources.AppUser;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@Component
public class JobNodeAuthHandler implements AnnotationHandlerInterface{


    @Autowired
    private RepositoryFactory repositoryFactory;

    @Override
    public boolean authorize(JoinPoint joinPoint, Method method, Annotation annotation, Authentication authentication) {


        AuthorizeJobRoles authorizeProjectRoles = (AuthorizeJobRoles)annotation;
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());

        //find projectId - argument with ProjectId annotation, and jodNodeId in the same way
        Optional<String> projectId = Optional.empty();
        Optional<String> jobNodeId = Optional.empty();
        int index = 0;
        for(Parameter parameter : method.getParameters()){
            if(parameter.isAnnotationPresent(ProjectId.class)){
                projectId = Optional.of((String)joinPoint.getArgs()[index]);
            }
            if(parameter.isAnnotationPresent(JobNodeId.class)){
                jobNodeId = Optional.of((String)joinPoint.getArgs()[index]);
            }
            if(jobNodeId.isPresent() && projectId.isPresent()) break;
            index++;
        }
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId.orElseThrow(RuntimeException::new));
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId.orElseThrow(RuntimeException::new));

        if(!jobNode.getProject().equals(project)) throw new RuntimeException();
        
        return jobNode.getPrivileges().get(appUser.getUsername()).getList().stream().anyMatch(role -> {
            for(JobNodePrivilege projectPrivilege : authorizeProjectRoles.roles()){
                if(projectPrivilege == role) return true;
            }
            return false;
        });
    }
    
}
