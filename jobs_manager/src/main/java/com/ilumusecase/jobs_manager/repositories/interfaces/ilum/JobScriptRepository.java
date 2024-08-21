package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Validated
public interface JobScriptRepository {

    public List<JobScript> retrieveJobScriptsOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String publisher, 
        @Min(1) Integer pageSize, 
        @Min(0) Integer pageNumber
    );

    public long countJobScriptsOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String publisher
    );
    
    public List<JobScript> retrieveAllJobScripts();
    public Optional<JobScript> retrieveJobScriptById(String id);
    public JobScript updateFullJobScript(@Valid JobScript jobScript);
    public void deleteJobScript(String id);
    
    public List<JobScript> retrieveJobScriptsByJobsFileId(String jobsFileId);
    public List<JobScript> retrieveJobScriptsByJobNodeId(String jobNodeId);

} 