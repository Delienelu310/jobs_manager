package com.ilumusecase.resources;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelList {
    public String id;
    public List<ChannelDTO> channelList = new ArrayList<>();


}
