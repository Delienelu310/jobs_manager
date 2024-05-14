package com.ilumusecase.jobs_manager.security.authorizationAnnotationsHandlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeRoles;

@Component
public class AuthAnnotationHandlerFactory {

    private Map<Class<?>, AnnotationHandlerInterface> handlersMap = new HashMap<>();


    public AuthAnnotationHandlerFactory(
        RoleAuthHandler rolesAuthHandler,
        ProjectAuthHandler projectAuthHandler,
        JobNodeAuthHandler jobnNodeAuthHandler
    ){
        handlersMap.put(AuthorizeRoles.class, rolesAuthHandler);
        handlersMap.put(AuthorizeProjectRoles.class, projectAuthHandler);
        handlersMap.put(AuthorizeJobRoles.class, jobnNodeAuthHandler); 
    }

    public Optional<AnnotationHandlerInterface> getAuthAnnotationHandler(Class<?> annotation){
        return Optional.ofNullable(handlersMap.get(annotation));
    }
    
}
