package com.ilumusecase.jobs_manager.repositories.interfaces.abstraction;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.abstraction.Channel;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface ChannelsRepository {

    public Channel retrieveById(String id);
    public List<Channel> retrieveAll();
    public List<Channel> retrieveAllByProjectId(String projectId);

    public Channel createChannel(Project project, @NotNull @Valid ChannelDetails channelDetails);

    public Channel updateChannel(String id, @NotNull @Valid ChannelDetails channelDetails);
    public Channel updateChannelFull(@NotNull @Valid Channel channel);

    public void deleteChannelById(String id);

} 
