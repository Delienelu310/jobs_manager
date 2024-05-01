package com.ilumusecase.resources;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobNodeDTO {

    public String id;
    public JobNodeDetails jobNodeDetails;

    public Map<String, ChannelList> input;
    public Map<String, ChannelList> output;

}
