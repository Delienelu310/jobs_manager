package com.ilumusecase.jobs_manager.json_mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.json_mappers.abstraction.ChannelJsonMapper;


@Component
public class JsonMappersFactory {

    @Autowired
    private ChannelJsonMapper channelJsonMapper;
    public ChannelJsonMapper getChannelJsonMapper() {
        return channelJsonMapper;
    }
    
}
