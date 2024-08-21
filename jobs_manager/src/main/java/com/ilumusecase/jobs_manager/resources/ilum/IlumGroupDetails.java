package com.ilumusecase.jobs_manager.resources.ilum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IlumGroupDetails {
    @Size(min = 3, max = 50)
    @NotNull
    private String name;
    @Size(min = 3, max = 500)
    private String description;
    
    @Min(0)
    @NotNull
    private Long startTime;
}
