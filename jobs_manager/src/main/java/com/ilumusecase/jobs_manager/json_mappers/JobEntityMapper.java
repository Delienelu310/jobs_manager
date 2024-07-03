package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

@Component
public class JobEntityMapper {
    
    public MappingJacksonValue getFullJobEntity(JobEntity jobEntity){
        return null;
    }

    public MappingJacksonValue getFullJobEntityList(List<JobEntity> jobs){
        return null;
    }

}
