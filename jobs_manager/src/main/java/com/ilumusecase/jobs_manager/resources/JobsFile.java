package com.ilumusecase.jobs_manager.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobsFile {
    
    @Id
    private String id;

    @DBRef(lazy = true)
    private JobNode jobNode;

    @DBRef(lazy = true)
    private AppUser author;

    private String extension;
    private List<String> jobClassesPaths = new ArrayList<>();
    private List<String> allClasses = new ArrayList<>();

    private JobDetails jobDetails;

}
