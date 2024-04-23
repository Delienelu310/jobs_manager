package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;

import com.ilumusecase.jobs_manager.resources.Channel;
import com.ilumusecase.jobs_manager.resources.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.Project;

public interface ChannelsRepository {

    public Channel retrieveById(String id);
    public List<Channel> retrieveAll();
    public List<Channel> retrieveAllByProjectId(String projectId);

    public Channel createChannel(Project project, ChannelDetails channelDetails);

    public Channel updateChannel(String id, ChannelDetails channelDetails);
    public Channel updateChannelFull(Channel channel);

    public void deleteChannelById(String id);

} 
