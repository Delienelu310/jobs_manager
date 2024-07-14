package com.ilumusecase.jobs_manager.resources.ui;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class ProjectGraph {

    @Id
    private String id;

    @DBRef(lazy = true)
    @JsonFilter("project_graph_project")
    private Project project;


    @DBRef(lazy = true)
    @JsonFilter("project_graph_vertices")
    private List<JobNodeVertice> vertices = new LinkedList<>();
}
