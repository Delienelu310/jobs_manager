package com.ilumusecase.jobs_manager.channelLaunchers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.JobsManagerApplication;

@Component
public class ChannelLauncherFactory {

    // @Autowired
    // private KafkaChannelLauncher kafkaChannelLauncher;

    @Autowired
    private Map<String, ChannelLauncher> channelLaunchersMap;
    // {
    //     channelLaunchersMap.put("kafka", kafkaChannelLauncher);
    // }

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);
    public ChannelLauncher getChannelLauncher(String type){
        logger.info(channelLaunchersMap.keySet().toString());        
        return channelLaunchersMap.get(type);
    }
}
