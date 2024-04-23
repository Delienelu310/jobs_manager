package com.ilumusecase.jobs_manager.resources;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobNode {

    @Id
    private Long id;

    private Long projectId;
    private String name;
}
