package com.ilumusecase.jobs_manager.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class Channel {
    @Id
    private String id;

    private ChannelDetails channelDetails;

    
    @DBRef
    @JsonFilter("project-reference")
    private Project project;


    @DBRef
    @JsonFilter("channel-plug-jobNode")
    private List<JobNode> inputJobs = new ArrayList<>();
    
    @DBRef
    @JsonFilter("channel-plug-jobNode")
    private List<JobNode> outputJobs = new ArrayList<>();
    
}
