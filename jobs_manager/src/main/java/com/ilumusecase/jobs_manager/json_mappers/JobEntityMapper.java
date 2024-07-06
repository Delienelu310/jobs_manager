package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;

@Component
public class JobEntityMapper {
    

    private final FilterProvider simpleJobEntityFilter = new SimpleFilterProvider()
        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("job_entity_ilum_group", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("ilum_group_jobs", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobEntityDetails", "ilumId"))
        .addFilter("job_entity_script", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_script_jobs_files", SimpleBeanPropertyFilter.serializeAll())
    ;
    
    
    public MappingJacksonValue getSimpleJobEntity(JobEntity jobEntity){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobEntity);
        wrapper.setFilters(simpleJobEntityFilter);

        return wrapper;
    }

    public MappingJacksonValue getSimpleJobEntity(List<JobEntity> jobs){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobs);
        wrapper.setFilters(simpleJobEntityFilter);

        return wrapper;
    }

}
