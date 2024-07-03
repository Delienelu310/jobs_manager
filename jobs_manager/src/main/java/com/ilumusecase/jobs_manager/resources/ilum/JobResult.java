package com.ilumusecase.jobs_manager.resources.ilum;

import java.time.LocalDateTime;

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
public class JobResult {

    @Id
    private String id;

    private Integer efficiency;
    private Integer quality;
    private String status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;


    @DBRef(lazy = true)
    private JobEntity tester;
    @DBRef(lazy = true)
    private JobEntity target;

    @DBRef(lazy = true)
    private Project project;
    @DBRef(lazy = true)
    private JobNode jobNode;

    
}