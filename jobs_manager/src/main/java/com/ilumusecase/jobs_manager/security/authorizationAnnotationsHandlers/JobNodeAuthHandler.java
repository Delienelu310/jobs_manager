package com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@Component
public class JobNodeAuthHandler implements AnnotationHandlerInterface{


    @Autowired
    private RepositoryFactory repositoryFactory;

    @Override
    public boolean authorize(JoinPoint joinPoint, Method method, Annotation annotation, Authentication authentication) {


        AuthorizeJobRoles authorizeJobRoles = (AuthorizeJobRoles)annotation;
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
        final String jobNodeIdValue = jobNodeId.orElseThrow(() -> new RuntimeException("Wrong usage of JobNodeAuthorization annotation : JobNodeId argument required"));
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId.orElseThrow(() -> new RuntimeException("Wrong usage of JobNodeAuthorization annotation: ProjectId argument requried")));
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeIdValue)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeIdValue));
;
        if(!jobNode.getProject().equals(project)) throw new RuntimeException();

        
        if(!jobNode.getPrivileges().containsKey(authentication.getName())) return false;
        //in case if roles are set null, we should just check if the user is in privileges map
        if(authorizeJobRoles.roles().length == 0 ){
            return true;
        }
        
        return jobNode.getPrivileges().get(appUser.getUsername()).getList().stream().anyMatch(role -> {
            for(JobNodePrivilege jobNodePrivilege : authorizeJobRoles.roles()){
                if(jobNodePrivilege == role) return true;
            }
            return false;
        });
    }
    
}
