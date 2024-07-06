package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.ilumusecase.jobs_manager.resources.abstraction.Channel;

@Component
public class ChannelJsonMapper {

    private final FilterProvider fullChannelFilter = new SimpleFilterProvider()
        .addFilter("channel-plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"))
        .addFilter("project-reference", SimpleBeanPropertyFilter.serializeAll())
        .addFilter("plug-channel", SimpleBeanPropertyFilter.filterOutAllExcept("id", "channelDetails"))
        .addFilter("plug-jobNode", SimpleBeanPropertyFilter.filterOutAllExcept("id", "jobNodeDetails"));

    public MappingJacksonValue getFullChannel(Channel channel){

        MappingJacksonValue wrapper = new MappingJacksonValue(channel); 
        wrapper.setFilters(fullChannelFilter);

        return wrapper;
    }

    public MappingJacksonValue getFullChannelList(List<Channel> channel){

        MappingJacksonValue wrapper = new MappingJacksonValue(channel);
        wrapper.setFilters(fullChannelFilter);

        return wrapper;
    }

}
