package com.ilumusecase.jobs_manager.json_mappers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Component("JobNodeVertice")
public class JobNodeVerticeJsonMapper implements ResourceJsonMapper{

    private final FilterProvider simplefFilterProvider = new SimpleFilterProvider()
        .addFilter("vertice_job_node", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
    ;

    private final Map<String, FilterProvider> filters = new HashMap<>();
    {
        filters.put("simple", simplefFilterProvider);
    }


    @Override
    public FilterProvider getFilterProvider(String type) {
        return filters.get(type);
    }
    
}
