package com.ilumusecase.jobs_manager.json_mappers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Component(value = "Project")
public class ProjectJsonMapper implements ResourceJsonMapper{

    private final FilterProvider fullProjectFilter = new SimpleFilterProvider()
        .addFilter("project", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("project-reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("plug-channel", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("node-plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
        .addFilter("channel-plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("plug-jobNode", SimpleBeanPropertyFilter.serializeAllExcept(
            "jobsFiles", "jobScripts", "jobResults", "jobEntities", "ilumGroups", "jobsQueue", "testingJobs"
        )
    );

    private final FilterProvider simpleProjectFilter = new SimpleFilterProvider()
        .addFilter("project", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails", "privileges"));
    


    private final Map<String, FilterProvider> filters = new HashMap<>();
    {
        filters.put("simple", simpleProjectFilter);
        filters.put("full", fullProjectFilter);
    }

    @Override
    public FilterProvider getFilterProvider(String type) {
        return filters.get(type);
    }

}
