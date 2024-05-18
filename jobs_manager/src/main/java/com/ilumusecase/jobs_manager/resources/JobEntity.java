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
    //className is author_username + authorCounter
    private String extension;

    private JobDetails jobDetails;

    @DBRef
    private JobNode jobNode;
    @DBRef
    private Project project;
    @DBRef 
    private AppUser author;


    
}
