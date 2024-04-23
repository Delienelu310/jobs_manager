package com.ilumusecase.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.Project;

@Component
public class ProjectJsonMapper {

    public MappingJacksonValue getFullProject(Project project){

        MappingJacksonValue wrapper = new MappingJacksonValue(project);
        FilterProvider filters = new SimpleFilterProvider()
            .addFilter("project-reference", SimpleBeanPropertyFilter.filterOutAllExcept("id"))
            .addFilter("plug-channel", SimpleBeanPropertyFilter.serializeAll());
        wrapper.setFilters(filters);

        return wrapper;
    }

    public MappingJacksonValue getFullProjectList(List<Project> project){

        MappingJacksonValue wrapper = new MappingJacksonValue(project);
        FilterProvider filters = new SimpleFilterProvider()
            .addFilter("project-reference", SimpleBeanPropertyFilter.filterOutAllExcept("id"))
            .addFilter("plug-channel", SimpleBeanPropertyFilter.serializeAll());
        wrapper.setFilters(filters);

        return wrapper;
    }

}
