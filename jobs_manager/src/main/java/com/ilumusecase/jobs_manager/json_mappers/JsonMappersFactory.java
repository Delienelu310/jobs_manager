package com.ilumusecase.jobs_manager.json_mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.json_mappers.abstraction.ChannelJsonMapper;
import com.ilumusecase.jobs_manager.json_mappers.abstraction.JobNodeJsonMapper;


@Component
public class JsonMappersFactory {

    @Autowired
    private ChannelJsonMapper channelJsonMapper;
    @Autowired
    private JobNodeJsonMapper jobNodeJsonMapper;
    
    public JobNodeJsonMapper getJobNodeJsonMapper() {
        return jobNodeJsonMapper;
    }
    public ChannelJsonMapper getChannelJsonMapper() {
        return channelJsonMapper;
    }
    
}
