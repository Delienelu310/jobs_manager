package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;
import java.util.Optional;

import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

public interface JobScriptRepository {

    public List<JobScript> retrieveJobScriptsOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String publisher, 
        Integer pageSize, 
        Integer pageNumber
    );

    public long countJobScriptsOfJobNode(
        String jobNodeId, 
        String query, 
        String extenstion, 
        String publisher
    );
    
    public List<JobScript> retrieveAllJobScripts();
    public Optional<JobScript> retrieveJobScriptById(String id);
    public JobScript updateFullJobScript(JobScript jobScript);
    public void deleteJobScript(String id);
    
    public List<JobScript> retrieveJobScriptsByJobsFileId(String jobsFileId);
    public List<JobScript> retrieveJobScriptsByJobNodeId(String jobNodeId);

} 