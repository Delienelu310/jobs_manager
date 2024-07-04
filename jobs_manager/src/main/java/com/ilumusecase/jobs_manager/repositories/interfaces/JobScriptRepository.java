package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;
import java.util.Optional;

import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

public interface JobScriptRepository {

    public List<JobScript> retrieveAllJobScripts();
    public Optional<JobScript> retrieveJobScriptById(String id);
    public JobScript updateFullJobScript(JobScript jobScript);
    public void deleteJobScript(String id);
    
    public List<JobScript> retrieveJobScriptsByJobsFileId(String jobsFileId);
    public List<JobScript> retrieveJobScriptsByJobNodeId(String jobNodeId);

} 