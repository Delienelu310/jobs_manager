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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.exceptions.security.NotAuthorizedException;
import com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers.AnnotationHandlerInterface;
import com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers.AuthAnnotationHandlerFactory;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthAdminRoleOnly;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.DisableAdminRoleAuth;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.DisableDefaultAuth;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.IgnoreAuthAspect;

@Aspect
@Component
public class AuthorizationAspect {

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

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
    

    @Pointcut("execution(* com.ilumusecase.jobs_manager.controllers..*(..))")
    public void allControllersPointcut(){}

    private boolean authorizeModerator(Authentication authentication){
        return authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().toString().equals("SCOPE_ROLE_MODERATOR"));
    }



    private boolean authorizeAdmin(Authentication authentication){
        authentication.getAuthorities().stream().map(auth -> auth.getAuthority()).forEach(logger::info);
        return authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("SCOPE_ROLE_ADMIN"));
    }

    private boolean authorizeDefault(Method method){
        return false;
    }

    @Before("allControllersPointcut()")
    public void authorizeControllerRequest(JoinPoint joinPoint){

        logger.info("Aspect was actually called: ");

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

            logger.info(annotation.annotationType().getSimpleName());
            logger.info(annotationHandlerInterface.get().getClass().getSimpleName());
            if(annotationHandlerInterface.get().authorize(joinPoint, method, annotation, authentication)) return;
        }

        throw new NotAuthorizedException();
    }
    
}
