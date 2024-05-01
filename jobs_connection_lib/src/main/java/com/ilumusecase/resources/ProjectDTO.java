package com.ilumusecase.resources;

import java.util.List;
import java.util.Map;

public class ProjectDTO {

    public String id;
    public ProjectDetails projectDetails;

    public List<ChannelDTO> channels;
    public List<JobNodeDTO> jobNodes; 

    public Map<String, ChannelDTO> inputChannels;
    public Map<String, ChannelDTO> outputChannels;
}
