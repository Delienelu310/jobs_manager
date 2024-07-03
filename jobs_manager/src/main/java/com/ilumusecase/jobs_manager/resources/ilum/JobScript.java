package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document
public class JobScript {
    
    @Id
    private String id;
    private String classFullName;
    private String extension;

    @DBRef(lazy = true)
    private List<JobsFile> jobsFiles = new LinkedList<>();
    
    @DBRef(lazy = true)
    private Project project;

    @DBRef(lazy = true)
    private JobNode jobNode;

    @DBRef(lazy = true)
    private AppUser author;
}
