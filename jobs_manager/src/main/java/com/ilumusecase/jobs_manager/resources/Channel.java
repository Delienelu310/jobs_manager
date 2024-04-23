package com.ilumusecase.jobs_manager.resources;

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

    @DBRef
    @JsonFilter("project-reference")
    private Project project;

    private ChannelDetails channelDetails;
    
}
