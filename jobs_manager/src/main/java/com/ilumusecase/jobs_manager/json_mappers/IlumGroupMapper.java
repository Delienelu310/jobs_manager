package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Component
public class IlumGroupMapper {

    private final FilterProvider simpleIlumGroupFilter = new SimpleFilterProvider()
        .addFilter("ilum_resource_project_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "projectDetails"))
        .addFilter("ilum_resource_job_node_reference", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("ilum_group_jobs", SimpleBeanPropertyFilter.serializeAllExcept("ilumGroup"))
        .addFilter("job_entity_job_script", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("job_script_jobs_files", SimpleBeanPropertyFilter.serializeAll())
    ;

    public MappingJacksonValue mapSimpleIlumGroup(IlumGroup ilumGroup){
        MappingJacksonValue wrapper = new MappingJacksonValue(ilumGroup);
        wrapper.setFilters(simpleIlumGroupFilter);

        return wrapper;
    }

    public MappingJacksonValue mapSimpleIlumGroupList(List<IlumGroup> ilumGroups){
        MappingJacksonValue wrapper = new MappingJacksonValue(ilumGroups);
        wrapper.setFilters(simpleIlumGroupFilter);

        return wrapper;
    }
}
