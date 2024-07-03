package com.ilumusecase.jobs_manager.resources.abstraction;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelDetails {
    private String name;
    private String type;
    private String[] headers;
}
