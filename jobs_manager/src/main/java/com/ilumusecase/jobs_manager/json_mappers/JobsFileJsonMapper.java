package com.ilumusecase.jobs_manager.json_mappers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Component("JobsFile")
public class JobsFileJsonMapper implements ResourceJsonMapper {

    private final FilterProvider simpleJobsFileFilter = new SimpleFilterProvider()
        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("ilum_resource_publisher", SimpleBeanPropertyFilter.filterOutAllExcept("username", "appUserDetails", "authorities"));

    private Map<String, FilterProvider> filters = new HashMap<>();
    {
        filters.put("simple", simpleJobsFileFilter);
    }

    @Override
    public FilterProvider getFilterProvider(String type) {
        return filters.get(type);
    }
}
