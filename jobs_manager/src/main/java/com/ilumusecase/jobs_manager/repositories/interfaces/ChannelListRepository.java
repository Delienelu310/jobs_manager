package com.ilumusecase.jobs_manager.repositories.interfaces;

import com.ilumusecase.jobs_manager.resources.ChannelList;

public interface ChannelListRepository {

    public ChannelList create();
    public ChannelList update(ChannelList channelList);
    public void delete(String id);
}
