package com.ilumusecase.jobs_manager.repositories.interfaces.abstraction;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.abstraction.ChannelList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface ChannelListRepository {

    public ChannelList create();
    public ChannelList update(@Valid @NotNull ChannelList channelList);
    public void delete(String id);
}
