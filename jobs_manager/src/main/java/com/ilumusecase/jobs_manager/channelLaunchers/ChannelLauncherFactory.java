package com.ilumusecase.jobs_manager.channelLaunchers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelType;

@Component
public class ChannelLauncherFactory {

    private Map<ChannelType, ChannelLauncher> channelLaunchersMap = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);
    public ChannelLauncherFactory(KafkaChannelLauncher kafkaChannelLauncher,
        MinioChannelLauncher minioChannelLauncher
    ) {
        channelLaunchersMap.put(ChannelType.KAFKA, kafkaChannelLauncher);
        channelLaunchersMap.put(ChannelType.MINIO, minioChannelLauncher);
    }
    public ChannelLauncher getChannelLauncher(ChannelType type){
        logger.info(channelLaunchersMap.keySet().toString());        
        return channelLaunchersMap.get(type);
    }
}
