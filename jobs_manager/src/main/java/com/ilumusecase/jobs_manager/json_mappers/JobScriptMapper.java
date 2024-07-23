package com.ilumusecase.jobs_manager.json_mappers;

import java.util.HashMap;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Component("JobScript")
public class JobScriptMapper implements ResourceJsonMapper{


    private final FilterProvider simpleJobScriptFilter = new SimpleFilterProvider()
        .addFilter("job_script_jobs_files", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("ilum_resource_publisher", SimpleBeanPropertyFilter.filterOutAllExcept("username", "appUserDetails", "authorities"))
    ;

    private java.util.Map<String, FilterProvider> filters = new HashMap<>();
    {
        filters.put("simple", simpleJobScriptFilter);
    }


    @Override
    public FilterProvider getFilterProvider(String type) {
        return filters.get(type);
    }
}
