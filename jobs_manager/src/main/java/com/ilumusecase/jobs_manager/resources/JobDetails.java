package com.ilumusecase.jobs_manager.resources;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobDetails {

    private String name;
    private String description;

    private String quality;
    private String effectivity;
    
}
