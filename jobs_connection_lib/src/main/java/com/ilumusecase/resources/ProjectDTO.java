package com.ilumusecase.resources;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDTO {

    public String id;
    public ProjectDetails projectDetails;

    public List<ChannelDTO> channels;
    public List<JobNodeDTO> jobNodes; 

    public Map<String, ChannelDTO> inputChannels;
    public Map<String, ChannelDTO> outputChannels;
}
