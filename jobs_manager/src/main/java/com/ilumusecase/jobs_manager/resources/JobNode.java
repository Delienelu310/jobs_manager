package com.ilumusecase.jobs_manager.resources;

import java.util.HashMap;
import java.util.List;
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
    
    @DBRef(lazy = true)
    @JsonFilter("node-plug-channel")
    private Map<String, List<Channel>> input = new HashMap<>();
    
    @DBRef(lazy = true)
    @JsonFilter("node-plug-channel")
    private Map<String, List<Channel>> output = new HashMap<>();

    
}
