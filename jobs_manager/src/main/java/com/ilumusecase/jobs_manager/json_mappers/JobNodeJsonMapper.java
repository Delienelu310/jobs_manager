package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.JobNode;

@Component
public class JobNodeJsonMapper {
    
    public MappingJacksonValue getFullJobNode(JobNode jobNode){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobNode);
        FilterProvider filters = new SimpleFilterProvider()
            .addFilter("node-plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
            .addFilter("project-reference", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
            .addFilter("plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"));
        wrapper.setFilters(filters);

        return wrapper;
    }

    public MappingJacksonValue getFullJobNodeList(List<JobNode> jobNodes){
        MappingJacksonValue wrapper = new MappingJacksonValue(jobNodes);
        FilterProvider filters = new SimpleFilterProvider()
            .addFilter("node-plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
            .addFilter("project-reference", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
            .addFilter("plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"));
        wrapper.setFilters(filters);

        return wrapper;
    }
}
