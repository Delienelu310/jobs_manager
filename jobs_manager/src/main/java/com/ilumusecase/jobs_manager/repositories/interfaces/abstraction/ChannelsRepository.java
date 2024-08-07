package com.ilumusecase.jobs_manager.repositories.interfaces.abstraction;

import java.util.List;

import com.ilumusecase.jobs_manager.resources.abstraction.Channel;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

public interface ChannelsRepository {

    public Channel retrieveById(String id);
    public List<Channel> retrieveAll();
    public List<Channel> retrieveAllByProjectId(String projectId);

    public Channel createChannel(Project project, ChannelDetails channelDetails);

    public Channel updateChannel(String id, ChannelDetails channelDetails);
    public Channel updateChannelFull(Channel channel);

    public void deleteChannelById(String id);

} 
