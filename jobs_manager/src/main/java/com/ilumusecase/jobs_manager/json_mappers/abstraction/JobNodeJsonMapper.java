package com.ilumusecase.jobs_manager.json_mappers.abstraction;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.json_mappers.ResourceJsonMapper;

@Component("JobNode")
public class JobNodeJsonMapper implements ResourceJsonMapper {

    private final FilterProvider ilumGroupInformationFilter = new SimpleFilterProvider()
        .addFilter("node-plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
        .addFilter("project-reference", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("project", SimpleBeanPropertyFilter.filterOutAll())
        .addFilter("plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
        .addFilter("plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))

        .addFilter("job_node_jobs_files", SimpleBeanPropertyFilter.filterOutAll())
        .addFilter("job_node_job_scripts", SimpleBeanPropertyFilter.filterOutAll())
        .addFilter("job_node_job_results", SimpleBeanPropertyFilter.filterOutAll())
        .addFilter("job_node_job_entities", SimpleBeanPropertyFilter.filterOutAll())
        .addFilter("job_node_jobs_queue", SimpleBeanPropertyFilter.filterOutAll())

        .addFilter("job_node_ilum_groups", SimpleBeanPropertyFilter.serializeAll())

        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("ilum_resource_publisher", SimpleBeanPropertyFilter.filterOutAllExcept("username", "appUserDetails", "authorities"))
        .addFilter("ilum_group_jobs", SimpleBeanPropertyFilter.serializeAllExcept("ilumGroup"))
        .addFilter("job_entity_job_script", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_script_jobs_files", SimpleBeanPropertyFilter.filterOutAll())
    ;

    private final FilterProvider fullJobNodeFilter = new SimpleFilterProvider()
        .addFilter("node-plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
        .addFilter("project-reference", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("project", SimpleBeanPropertyFilter.filterOutAll())
        .addFilter("plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
        .addFilter("plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))

        .addFilter("job_node_jobs_files", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_node_job_scripts", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_node_job_results", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_node_job_entities", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_node_jobs_queue", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_node_ilum_groups", SimpleBeanPropertyFilter.serializeAll())

        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("ilum_resource_publisher", SimpleBeanPropertyFilter.filterOutAllExcept("username", "appUserDetails", "authorities"))

        .addFilter("job_script_jobs_files", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("ilum_group_jobs", SimpleBeanPropertyFilter.serializeAllExcept("ilumGroup"))
        .addFilter("job_entity_ilum_group", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_entity_job_script", SimpleBeanPropertyFilter.serializeAll())
    ;

    private final Map<String, FilterProvider> filters = new HashMap<>();
    {  

        filters.put("full", fullJobNodeFilter);
        filters.put("ilumGroup", ilumGroupInformationFilter);
    }

    @Override
    public FilterProvider getFilterProvider(String type) {
        return this.filters.get(type);
    }



}
