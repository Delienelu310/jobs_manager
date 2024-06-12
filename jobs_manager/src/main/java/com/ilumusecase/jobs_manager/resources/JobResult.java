package com.ilumusecase.jobs_manager.resources;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Data;
import lombok.NoArgsConstructor;

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
    private JobEntity testJobCreator;
}
