package com.ilumusecase.jobs_manager.resources.abstraction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectDetails {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
    @Size(min = 3, max = 500)
    private String description;
}
