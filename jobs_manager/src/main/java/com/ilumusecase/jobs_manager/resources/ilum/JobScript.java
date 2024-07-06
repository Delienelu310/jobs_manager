package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
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
    @JsonFilter("job_script_jobs_files")
    private List<JobsFile> jobsFiles = new LinkedList<>();
    
    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_project_reference")
    private Project project;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_job_node_reference")
    private JobNode jobNode;

    @DBRef(lazy = true)
    private AppUser author;
}
