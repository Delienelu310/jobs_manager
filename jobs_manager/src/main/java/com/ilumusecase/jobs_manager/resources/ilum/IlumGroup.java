package com.ilumusecase.jobs_manager.resources.ilum;

import java.time.LocalDateTime;
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

    private IlumGroupConfiguraion ilumGroupConfiguraion;

    private int currentIndex = 0;
    private int currentTestingIndex = 0;
    private String mod;

    @DBRef(lazy = true)
    private JobEntity currentJob;
    private LocalDateTime currentStartTime;

    @DBRef(lazy = true)    
    private List<JobEntity> jobs = new ArrayList<>();

    @DBRef(lazy = true)
    private List<JobEntity> testingJobs = new ArrayList<>();


    @DBRef(lazy = true)
    private Project project;

    @DBRef(lazy = true)
    private JobNode jobNode;

    
    
}
