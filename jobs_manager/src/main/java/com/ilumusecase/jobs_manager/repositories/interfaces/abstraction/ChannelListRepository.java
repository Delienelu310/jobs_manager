package com.ilumusecase.jobs_manager.repositories.interfaces.abstraction;

import com.ilumusecase.jobs_manager.resources.abstraction.ChannelList;

public interface ChannelListRepository {

    public ChannelList create();
    public ChannelList update(ChannelList channelList);
    public void delete(String id);
}
