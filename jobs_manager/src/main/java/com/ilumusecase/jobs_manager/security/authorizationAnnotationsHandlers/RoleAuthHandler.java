package com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.security.Roles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeRoles;

@Component
public class RoleAuthHandler implements AnnotationHandlerInterface{


    public boolean authorize(JoinPoint joinPoint, Method method, Annotation annotation, Authentication authentication){
        AuthorizeRoles authorizeRoles = (AuthorizeRoles)annotation;

        if(authorizeRoles.roles().length == 0) return true;

        return authentication.getAuthorities().stream().map(auth -> auth.getAuthority()).anyMatch(role -> {
            for(Roles roleAllowed : authorizeRoles.roles()){
                if(role.toString().equals("SCOPE_ROLE_" + roleAllowed.toString())) return true;
            }
            return false;
        });
    }

}
