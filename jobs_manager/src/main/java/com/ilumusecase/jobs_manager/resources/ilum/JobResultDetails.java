package com.ilumusecase.jobs_manager.resources.ilum;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobResultDetails {

    private String errorMessage;
    private String errorStackTrace;
    
    private String resultStr;

    private Long quality;
    
}
