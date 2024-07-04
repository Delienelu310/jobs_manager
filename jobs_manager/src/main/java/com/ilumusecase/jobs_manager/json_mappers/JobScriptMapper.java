package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

@Component
public class JobScriptMapper {
    
    public MappingJacksonValue mapJobScriptFull(JobScript jobScript){
        return null;
    }

    public MappingJacksonValue mapJobScriptListFull(List<JobScript> jobScripts){
        return null;
    }
}
