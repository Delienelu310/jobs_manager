package com.ilumusecase.jobs_manager.resources;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class Project {
    
    @Id
    private Long id;

    private ProjectDetails projectDetails;

    private Map<String, Channel> inputChannels;
    private Map<String, Channel> outputChannels;
}
