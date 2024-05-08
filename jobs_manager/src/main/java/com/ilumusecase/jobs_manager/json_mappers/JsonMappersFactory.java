package com.ilumusecase.jobs_manager.json_mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class JsonMappersFactory {

    @Autowired
    private ProjectJsonMapper projectJsonMapper;
    @Autowired
    private ChannelJsonMapper channelJsonMapper;
    @Autowired
    private JobNodeJsonMapper jobNodeJsonMapper;
    @Autowired
    private AppUserJsonMapper appUserJsonMapper;
    @Autowired
    private JobEntityMapper jobEntityMapper;

    
    public JobEntityMapper getJobEntityMapper() {
        return jobEntityMapper;
    }
    public JobNodeJsonMapper getJobNodeJsonMapper() {
        return jobNodeJsonMapper;
    }
    public ProjectJsonMapper getProjectJsonMapper() {
        return projectJsonMapper;
    }
    public ChannelJsonMapper getChannelJsonMapper() {
        return channelJsonMapper;
    }
    
    public AppUserJsonMapper getAppUserJsonMapper() {
        return appUserJsonMapper;
    }
    
}
