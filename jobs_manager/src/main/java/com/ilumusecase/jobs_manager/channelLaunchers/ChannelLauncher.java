package com.ilumusecase.jobs_manager.channelLaunchers;

import com.ilumusecase.jobs_manager.resources.abstraction.Channel;

public interface ChannelLauncher {
    public void launchChannel(Channel channel);
    public void stopChannel(Channel channel);
}
