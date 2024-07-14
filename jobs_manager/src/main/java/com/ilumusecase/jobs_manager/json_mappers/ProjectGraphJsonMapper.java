package com.ilumusecase.jobs_manager.json_mappers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Component(value = "ProjectGraph")
public class ProjectGraphJsonMapper implements ResourceJsonMapper{

  
    private final FilterProvider graphFilterProvider = new SimpleFilterProvider()
        .addFilter("project_graph_project", SimpleBeanPropertyFilter.filterOutAllExcept("id"))
        .addFilter("project_graph_vertices", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("vertice_job_node", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails", "input", "output"))
        .addFilter("node-plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"));
    

    private final Map<String, FilterProvider> filters = new HashMap<>();
    {
        filters.put("graph", graphFilterProvider);
    }

    @Override
    public FilterProvider getFilterProvider(String type) {
        return filters.get(type);
    }
}
