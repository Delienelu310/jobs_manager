package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;

@Component
public class JobsFileJsonMapper {

    private final FilterProvider simpleJobsFileFilter = new SimpleFilterProvider()
        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"));

    public MappingJacksonValue getSimpleJobsFile(JobsFile jobsFile){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobsFile);
        wrapper.setFilters(simpleJobsFileFilter);

        return wrapper;
    }

    public MappingJacksonValue getSimpleJobsFilesList(List<JobsFile> jobsFiles){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobsFiles);
        wrapper.setFilters(simpleJobsFileFilter);

        return wrapper;
    }
}
