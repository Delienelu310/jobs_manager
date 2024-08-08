package com.ilumusecase.jobs_manager.resources.abstraction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelDetails {

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
    @NotNull
    private ChannelType type;
    @NotEmpty
    @Size(min = 1, max = 20)
    private String[] headers;
}
