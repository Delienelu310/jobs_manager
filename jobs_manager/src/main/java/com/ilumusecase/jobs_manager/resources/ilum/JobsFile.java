package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobsFile {
    
    @Id
    private String id;
    private String extension;
    private JobsFileDetails jobDetails;

    @DBRef(lazy = true)
    private Project project;

    @DBRef(lazy = true)
    private JobNode jobNode;

    @DBRef(lazy = true)
    private AppUser publisher;
    
    private Set<String> allClasses = new HashSet<>();

}
