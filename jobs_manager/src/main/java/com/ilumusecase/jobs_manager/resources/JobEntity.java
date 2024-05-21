package com.ilumusecase.jobs_manager.resources;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobEntity {

    @Id
    private String id;

    private String groupId;
    private String ilumId;
    private String extension;
    private String classPath;

    private JobDetails jobDetails;

    @DBRef
    private JobNode jobNode;
    @DBRef
    private Project project;
    @DBRef 
    private AppUser author;


    
}