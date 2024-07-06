package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

@Component
public class JobScriptMapper {


    private final FilterProvider simpleJobScriptFilter = new SimpleFilterProvider()
        .addFilter("job_script_jobs_files", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"));
    ;
    
    public MappingJacksonValue mapSimpleJobScript(JobScript jobScript){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobScript);
        wrapper.setFilters(simpleJobScriptFilter);

        return wrapper;
    }

    public MappingJacksonValue mapSimpleJobScriptsList(List<JobScript> jobScripts){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobScripts);
        wrapper.setFilters(simpleJobScriptFilter);

        return wrapper;
    }
}
