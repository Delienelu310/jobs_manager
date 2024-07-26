package com.ilumusecase.jobs_manager.json_mappers.ilum;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.json_mappers.ResourceJsonMapper;

@Component("JobEntity")
public class JobEntityMapper implements ResourceJsonMapper{
    

    private final FilterProvider simpleJobEntityFilter = new SimpleFilterProvider()
        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("ilum_resource_publisher", SimpleBeanPropertyFilter.filterOutAllExcept("username", "appUserDetails", "authorities"))
        .addFilter("job_entity_job_script", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_entity_ilum_group", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("ilum_group_jobs", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobEntityDetails", "ilumId"))
        .addFilter("job_script_jobs_files", SimpleBeanPropertyFilter.serializeAll())
    ;
  
    private final Map<String, FilterProvider> filters = new HashMap<>();
    {
        filters.put("simple", simpleJobEntityFilter);
    }
    @Override
    public FilterProvider getFilterProvider(String type) {
        return filters.get(type);
    }

}
