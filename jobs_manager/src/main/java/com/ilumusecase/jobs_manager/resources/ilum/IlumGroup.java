package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class IlumGroup {
    
    @Id
    private String id;
    private String ilumId;

    private int currentIndex = 0;
    private int currentTestingIndex = 0;

    @DBRef(lazy = true)    
    private List<JobEntity> jobs = new ArrayList<>();

    @DBRef(lazy = true)
    private List<JobEntity> testingJobs = new ArrayList<>();


    @DBRef(lazy = true)
    private Project project;

    @DBRef(lazy = true)
    private JobNode jobNode;

    
    
}
