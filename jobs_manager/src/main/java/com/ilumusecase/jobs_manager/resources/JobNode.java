package com.ilumusecase.jobs_manager.resources;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobNode {

    @Id
    private String id;

    private JobNodeDetails jobNodeDetails;

    @DBRef
    @JsonFilter("project-reference")
    private Project project;
    
    @DBRef
    @JsonFilter("node-plug-channel")
    private Map<String, Channel> input;
    
    @DBRef
    @JsonFilter("node-plug-channel")
    private Map<String, Channel> output;

    
}
