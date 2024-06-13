package com.ilumusecase.jobs_manager.json_mappers;

import java.util.List;

import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.resources.JobsFile;

@Component
public class JobsFileJsonMapper {
    public MappingJacksonValue getFullJobsFile(JobsFile jobsFile){
        return null;
    }

    public MappingJacksonValue getFullJobsFilesList(List<JobsFile> jobsFiles){
        return null;
    }
}
