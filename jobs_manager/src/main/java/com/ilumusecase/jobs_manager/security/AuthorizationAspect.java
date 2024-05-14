package com.ilumusecase.jobs_manager.security;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers.AnnotationHandlerInterface;
import com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers.AuthAnnotationHandlerFactory;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthAdminRoleOnly;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.DisableAdminRoleAuth;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.DisableDefaultAuth;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.IgnoreAuthAspect;

@Aspect
@Component
public class AuthorizationAspect {

    @Autowired 
    private AuthAnnotationHandlerFactory authAnnotationHandlerFactory;


    private Optional<Method> getJoinPointMethod(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        Method method = null;
        if (signature != null && signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            method = methodSignature.getMethod();
            
        }
        return Optional.ofNullable(method);
    }
    

    @Pointcut("exectuion(* com.ilumusecase.jobs_manager.controllers..*(..))")
    public void allControllersPointcut(){}

    private boolean authorizeModerator(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_MODERATOR"));
    }

    private boolean authorizeAdmin(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(auth -> auth.toString().equals("ROLE_ADMIN"));
    }

    private boolean authorizeDefault(Method method){
        return true;
    }

    @Before("allControllersPointcut()")
    public void authorizeControllerRequest(JoinPoint joinPoint){

        Method method = getJoinPointMethod(joinPoint).orElseThrow(RuntimeException::new);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Set<Annotation> usedAnnotations = Arrays.stream(method.getAnnotations()).collect(Collectors.toSet());
        Set<Class<?>> annotationsTypes = usedAnnotations.stream().map(annotation -> annotation.annotationType()).collect(Collectors.toSet());

        if(annotationsTypes.contains(IgnoreAuthAspect.class)) return;

        if(!annotationsTypes.contains(DisableAdminRoleAuth.class)){
            if(authorizeAdmin(authentication)) return;
            if(!annotationsTypes.contains(AuthAdminRoleOnly.class) && authorizeModerator(authentication)) return;
        }

        if( !annotationsTypes.contains(DisableDefaultAuth.class) &&  authorizeDefault(method)){
            return;
        }

        //for role annotations?
        for(Annotation annotation : usedAnnotations){
            
            Optional<AnnotationHandlerInterface> annotationHandlerInterface = authAnnotationHandlerFactory.getAuthAnnotationHandler(annotation.annotationType());
            if(annotationHandlerInterface.isEmpty()) continue;

            if(annotationHandlerInterface.get().authorize(joinPoint, method, annotation, authentication)) return;
        }

        return;
    }
    
}
