package com.ilumusecase.jobs_manager.json_mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class JsonMappersFactory {

    @Autowired
    private ProjectJsonMapper projectJsonMapper;
    @Autowired
    private ChannelJsonMapper channelJsonMapper;
    
}
