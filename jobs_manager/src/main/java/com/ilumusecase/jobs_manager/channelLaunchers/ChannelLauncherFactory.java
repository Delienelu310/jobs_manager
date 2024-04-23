package com.ilumusecase.jobs_manager.channelLaunchers;

import java.util.Map;

public class ChannelLauncherFactory {

    private Map<String, ChannelLauncher> channelLaunchersMap;

    public ChannelLauncher getChannelLauncher(String type){
        return channelLaunchersMap.get(type);
    }
}
