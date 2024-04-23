package com.ilumusecase.jobs_manager.resources;

import java.util.HashMap;
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
public class Project {
    
    @Id
    private String id;

    private ProjectDetails projectDetails;

    @DBRef
    @JsonFilter("plug-channel")
    private Map<String, Channel> inputChannels = new HashMap<>();
    @JsonFilter("plug-channel")
    private Map<String, Channel> outputChannels = new HashMap<>();
}
