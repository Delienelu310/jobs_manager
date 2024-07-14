package com.ilumusecase.jobs_manager.resources.ui;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobNodeVertice {
    @Id
    private String id;

    @DBRef(lazy = true)
    @JsonFilter("vertice_job_node")
    private JobNode jobNode;

    private int x;
    private int y;
}
