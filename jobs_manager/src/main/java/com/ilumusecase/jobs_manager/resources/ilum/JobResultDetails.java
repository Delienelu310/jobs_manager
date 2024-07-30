package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobResultDetails {

    private String errorMessage;
    private String errorStackTrace;
    
    private String resultStr;

    private Map<String, String> metrics = new HashMap<>();
    
}
