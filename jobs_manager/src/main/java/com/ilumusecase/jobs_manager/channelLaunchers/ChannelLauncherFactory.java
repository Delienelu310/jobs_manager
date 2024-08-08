package com.ilumusecase.jobs_manager.channelLaunchers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelType;

@Component
public class ChannelLauncherFactory {

    @Autowired
    private KafkaChannelLauncher kafkaChannelLauncher;
    @Autowired
    private MinioChannelLauncher minioChannelLauncher;

    @Autowired
    private Map<ChannelType, ChannelLauncher> channelLaunchersMap;
    {
        channelLaunchersMap.put(ChannelType.KAFKA, kafkaChannelLauncher);
        channelLaunchersMap.put(ChannelType.MINIO, minioChannelLauncher);
    }

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);
    public ChannelLauncher getChannelLauncher(ChannelType type){
        logger.info(channelLaunchersMap.keySet().toString());        
        return channelLaunchersMap.get(type);
    }
}
