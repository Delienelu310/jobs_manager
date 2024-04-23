package com.ilumusecase.jobs_manager.resources;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private Map<String, Channel> inputChannels = new HashMap<>();
    private Map<String, Channel> outputChannels = new HashMap<>();
}
