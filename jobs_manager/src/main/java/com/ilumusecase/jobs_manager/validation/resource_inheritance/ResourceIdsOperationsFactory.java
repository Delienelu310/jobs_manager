package com.ilumusecase.jobs_manager.validation.resource_inheritance;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
public class ResourceIdsOperationsFactory {

    private List<ResourceIdsOperationsInstance> resourceIdsOperationsInstances;
    private Set<Class< ? extends Annotation>>resourceAnnotations;

    public ResourceIdsOperationsFactory(List<ResourceIdsOperationsInstance> resourceIdsOperationsInstances) {
        this.resourceIdsOperationsInstances = resourceIdsOperationsInstances;
        this.resourceAnnotations = resourceIdsOperationsInstances.stream().map(instance -> instance.getOperationsTarget()).collect(Collectors.toSet());
    }


    private Optional<Method> getJoinPointMethod(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        Method method = null;
        if (signature != null && signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            method = methodSignature.getMethod();
            
        }
        return Optional.ofNullable(method);
    }


    public Map<Class<? extends Annotation>, List<String>> extractIds(JoinPoint joinPoint){

        
        Method method = getJoinPointMethod(joinPoint).orElseThrow(() -> 
            new RuntimeException("Resource inheritance validation aspect was applied to the wrong element : " + joinPoint.getSignature().toShortString()));
        
            Map<Class<? extends Annotation>, List<String>> result = new HashMap<>();

        int index = 0;
        for(Parameter parameter : method.getParameters()){
            
            boolean isString = String.class.isAssignableFrom(parameter.getType());

            for(Annotation annotation : parameter.getAnnotations()){
                if(!resourceAnnotations.contains(annotation.annotationType())) continue;

                if(!isString) throw new RuntimeException("Reource Id Annotation must not be applied to parameter of non-string type: " + parameter.getName());
            
                if(!result.containsKey(annotation.getClass())){
                    result.put(annotation.getClass(), new LinkedList<>());
                }
                result.get(annotation.getClass()).add((String)joinPoint.getArgs()[index]);
            
            }

            index++;
        }
        

        return result;
    }

    public void applyValidations(Map< Class<? extends Annotation> , List<String>> ids){
        for(ResourceIdsOperationsInstance instance : resourceIdsOperationsInstances){
            instance.validateResourceInheritance(ids);
        }
    }

}
