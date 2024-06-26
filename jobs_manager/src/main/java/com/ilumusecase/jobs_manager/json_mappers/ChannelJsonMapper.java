package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.Channel;

@Component
public class ChannelJsonMapper {

    public MappingJacksonValue getFullChannel(Channel channel){

        MappingJacksonValue wrapper = new MappingJacksonValue(channel);
        FilterProvider filters = new SimpleFilterProvider()
            .addFilter("channel-plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
            .addFilter("project-reference", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
            .addFilter("plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"));
        wrapper.setFilters(filters);

        return wrapper;
    }

    public MappingJacksonValue getFullChannelList(List<Channel> channel){

        MappingJacksonValue wrapper = new MappingJacksonValue(channel);
        FilterProvider filters = new SimpleFilterProvider()
            .addFilter("channel-plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
            .addFilter("project-reference", SimpleBeanPropertyFilter.serializeAll())
            .addFilter("plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
            .addFilter("plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"));
        wrapper.setFilters(filters);

        return wrapper;
    }

}
