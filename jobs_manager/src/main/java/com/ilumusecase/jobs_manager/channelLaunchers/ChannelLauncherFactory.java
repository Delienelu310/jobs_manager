package com.ilumusecase.jobs_manager.channelLaunchers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ChannelLauncherFactory {

    private Map<String, ChannelLauncher> channelLaunchersMap = new HashMap<>();
    {
        channelLaunchersMap.put("kafka", new KafkaChannelLauncher());
    }

    public ChannelLauncher getChannelLauncher(String type){
        return channelLaunchersMap.get(type);
    }
}
