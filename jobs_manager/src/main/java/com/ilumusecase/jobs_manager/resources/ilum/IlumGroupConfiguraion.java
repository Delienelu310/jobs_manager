package com.ilumusecase.jobs_manager.resources.ilum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IlumGroupConfiguraion {
    @NotNull
    @Min(1)
    private long maxJobDuration;
}
