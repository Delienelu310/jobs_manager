package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;

@Component
public class JobNodeJsonMapper {


    private final FilterProvider fullJobNodeFilter = new SimpleFilterProvider()
        .addFilter("node-plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
        .addFilter("project-reference", SimpleBeanPropertyFilter.serializeAll())
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

        .addFilter("job_script_jobs_files", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("ilum_groups_jobs", SimpleBeanPropertyFilter.serializeAllExcept("ilumGroup"))
        .addFilter("job_entity_ilum_group", SimpleBeanPropertyFilter.serializeAll())
    ;

    public MappingJacksonValue getFullJobNode(JobNode jobNode){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobNode);
        wrapper.setFilters(fullJobNodeFilter);

        return wrapper;
    }

    public MappingJacksonValue getFullJobNodeList(List<JobNode> jobNodes){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobNodes);
        wrapper.setFilters(fullJobNodeFilter);

        return wrapper;
    }
}
