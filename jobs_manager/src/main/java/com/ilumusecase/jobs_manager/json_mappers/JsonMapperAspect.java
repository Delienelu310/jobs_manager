package com.ilumusecase.jobs_manager.json_mappers;

import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.ilumusecase.jobs_manager.JobsManagerApplication;

@Aspect
@Component
public class JsonMapperAspect {

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    @Autowired
    private Map<String, ResourceJsonMapper> mappers;
    
    
    private Optional<Method> getJoinPointMethod(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        Method method = null;
        if (signature != null && signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            method = methodSignature.getMethod();
            
        }
        return Optional.ofNullable(method);
    }

    @Around("execution(public Object com.ilumusecase.jobs_manager.controllers..*(..))")
    public Object mapObject(ProceedingJoinPoint joinPoint) throws Throwable{
        Object result = joinPoint.proceed();

        if(result == null) return result;

        Method method = getJoinPointMethod(joinPoint).orElseThrow(RuntimeException::new);
        if(!method.isAnnotationPresent(JsonMapperRequest.class)) return result;

        JsonMapperRequest jsonMapperRequest = method.getAnnotation(JsonMapperRequest.class);

        String type = jsonMapperRequest.type();
        String resource = jsonMapperRequest.resource();

        FilterProvider filterProvider = mappers.get(resource).getFilterProvider(type);
        MappingJacksonValue wrapper = new MappingJacksonValue(result);
        wrapper.setFilters(filterProvider);

        return wrapper;
    }

}
