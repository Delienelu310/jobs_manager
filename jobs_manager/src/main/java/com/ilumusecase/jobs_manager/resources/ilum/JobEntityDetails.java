package com.ilumusecase.jobs_manager.resources.ilum;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobEntityDetails {

    @Min(3)
    @Max(50)
    @NotBlank
    private String name;
    @Min(3)
    @Max(500)
    private String description;
}
