package com.ilumusecase.jobs_manager.channelLaunchers;

import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.resources.Channel;

@Component("minio")
public class MinioChannelLauncher implements ChannelLauncher{

    @Override
    public void launchChannel(Channel channel) {
        
    }

    @Override
    public void stopChannel(Channel channel) {
        
    }
    
}
