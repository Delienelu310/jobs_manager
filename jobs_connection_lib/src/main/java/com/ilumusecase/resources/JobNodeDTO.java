package com.ilumusecase.resources;

import java.util.Map;

public class JobNodeDTO {

    public String id;
    public JobNodeDetails jobNodeDetails;

    public Map<String, ChannelList> input;
    public Map<String, ChannelList> output;

}
