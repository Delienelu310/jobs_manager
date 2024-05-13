package com.ilumusecase.jobs_manager.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.AppUser;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.resources.ProjectPrivilege;

@Aspect
@Component
public class AuthorizationAspect {


    private boolean isAdminModerator(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_ADMIN") || auth.toString().equals("ROLE_MODERATOR"));
    }

    private boolean isAdmin(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_ADMIN"));
    }

    private boolean isProjectAdminModerator(Project project, AppUser appUser){
        return project.getPrivileges().get(appUser).getList().stream().anyMatch(pr -> pr == ProjectPrivilege.ADMIN || pr == ProjectPrivilege.MODERATOR);
    }
    
    @Autowired
    private RepositoryFactory repositoryFactory;

    @Pointcut("within(com.ilumusecase.jobs_manager.controllers.ProjectController) && " + 
        "!execution(* com.ilumusecase.jobs_manager.controllers.ProjectContoller.getAllProjects) && " +
        "!execution(* com.ilumusecase.jobs_manager.controllers.ProjectContoller.getProjectById) && " + 
        "!execution(* com.ilumusecase.jobs_manager.controllers.ProjectContoller.createProject) && " + 
        "!execution(* com.ilumusecase.jobs_manager.controllers.ProjectContoller.deleteProject)"
    )
    public void projectGeneralPointcut(){}

    @Pointcut("execution(* com.ilumusecase.jobs_manager.controllers.ProjectContoller.getProjectById)")
    public void projectViewerAccess(){}


    @Pointcut("execution()")
    public void authorizeAnyManager(){}

    @Before("projectViewerAccess()")
    public void authorizeViewerAccess(JoinPoint joinPoint){
        //first argument always must be project id
        String project_id = (String)joinPoint.getArgs()[0];

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(project_id);
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());
        if(
            !isAdminModerator(authentication)
            &&
            !project.getPrivileges().containsKey(appUser)
        ){
            throw new RuntimeException();
        }
    
    }

    @Before("projectGeneralPointcut()")
    public void authorizeGeneralProjectEndpoint(JoinPoint joinPoint){
    
        //first argument always must be project id
        String project_id = (String)joinPoint.getArgs()[0];

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(project_id);
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());
        
        if(
            !isAdminModerator(authentication)
            &&    
            !isProjectAdminModerator(project, appUser)
        ){   
            throw new RuntimeException("You dont have access to the endpoint");
        }


    
    }


    
}
