package com.ilumusecase.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelDTO {

    public String id;
    public ChannelDetails channelDetails;
}
