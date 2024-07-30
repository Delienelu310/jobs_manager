package com.ilumusecase.jobs_manager.json_mappers.ilum;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.json_mappers.ResourceJsonMapper;

@Component("JobResult")
public class JobResultMapper implements ResourceJsonMapper{


    private final FilterProvider simpleFilter = new SimpleFilterProvider()
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("job_result_job_script", SimpleBeanPropertyFilter.filterOutAllExcept("id", "classFullName"))
    ;

    private final Map<String, FilterProvider> filters = new HashMap<>();
    {
        filters.put("simple", simpleFilter);
    }

    @Override
    public FilterProvider getFilterProvider(String type) {
        return filters.get(type);
    }
    
}
