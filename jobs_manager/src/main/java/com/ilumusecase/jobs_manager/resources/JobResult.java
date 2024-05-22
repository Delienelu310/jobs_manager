package com.ilumusecase.jobs_manager.resources;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobResult {
    Integer efficiency;
    Integer quality;
    String status;

    LocalDateTime startTime;
    LocalDateTime endTime;
}
