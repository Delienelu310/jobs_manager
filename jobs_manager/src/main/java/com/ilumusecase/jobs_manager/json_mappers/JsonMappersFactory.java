package com.ilumusecase.jobs_manager.json_mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.json_mappers.abstraction.ChannelJsonMapper;
import com.ilumusecase.jobs_manager.json_mappers.abstraction.JobNodeJsonMapper;
import com.ilumusecase.jobs_manager.json_mappers.authorization.AppUserJsonMapper;
import com.ilumusecase.jobs_manager.json_mappers.ilum.JobEntityMapper;
import com.ilumusecase.jobs_manager.json_mappers.ilum.JobScriptMapper;
import com.ilumusecase.jobs_manager.json_mappers.ilum.JobsFileJsonMapper;


@Component
public class JsonMappersFactory {

    @Autowired
    private ChannelJsonMapper channelJsonMapper;
    @Autowired
    private JobNodeJsonMapper jobNodeJsonMapper;
    @Autowired
    private AppUserJsonMapper appUserJsonMapper;
    @Autowired
    private JobEntityMapper jobEntityMapper;
    @Autowired
    private JobsFileJsonMapper jobsFileJsonMapper;
    @Autowired
    private JobScriptMapper jobScriptMapper;
    
    public JobEntityMapper getJobEntityMapper() {
        return jobEntityMapper;
    }
    public JobNodeJsonMapper getJobNodeJsonMapper() {
        return jobNodeJsonMapper;
    }
    public ChannelJsonMapper getChannelJsonMapper() {
        return channelJsonMapper;
    }
    
    public AppUserJsonMapper getAppUserJsonMapper() {
        return appUserJsonMapper;
    }
    public JobsFileJsonMapper getJobsFileJsonMapper(){
        return jobsFileJsonMapper;
    }
    public JobScriptMapper getJobScriptMapper(){
        return jobScriptMapper;
    }
    
}
