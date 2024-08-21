package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobResultDetails {

    @Size(max = 500)
    private String errorMessage;
    @Size(max = 5000)
    private String errorStackTrace;
    
    @Size(max=500)
    private String resultStr;

    @Size(min = 1, max = 20)
    private Map<String, String> metrics = new HashMap<>();
    
}
