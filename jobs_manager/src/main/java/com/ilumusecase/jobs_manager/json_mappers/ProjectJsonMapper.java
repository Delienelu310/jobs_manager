package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

@Component
public class ProjectJsonMapper {

    private final FilterProvider fullProjectFilter = new SimpleFilterProvider()
        .addFilter("project-reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("plug-channel", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("node-plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
        .addFilter("channel-plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("plug-jobNode", SimpleBeanPropertyFilter.serializeAllExcept(
            "jobsFiles", "jobScripts", "jobResults", "jobEntities", "ilumGroups", "jobsQueue", "testingJobs"
        )
    );

    public MappingJacksonValue getFullProject(Project project){

        MappingJacksonValue wrapper = new MappingJacksonValue(project);
        wrapper.setFilters(fullProjectFilter);

        return wrapper;
    }

    public MappingJacksonValue getFullProjectList(List<Project> project){

        MappingJacksonValue wrapper = new MappingJacksonValue(project);
        wrapper.setFilters(fullProjectFilter);

        return wrapper;
    }

}
