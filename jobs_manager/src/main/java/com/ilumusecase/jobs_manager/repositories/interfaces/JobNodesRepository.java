package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.List;

import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.JobNodeDetails;
import com.ilumusecase.jobs_manager.resources.Project;

public interface JobNodesRepository{
    
    public List<JobNode> retrieveByProjectId(String projectId);
    public JobNode retrieveById(String id);
    public List<JobNode> retrieveAll();


    public JobNode createJobNode(Project project, JobNodeDetails jobNodeDetails);
    public JobNode updateJobNode(String id, JobNodeDetails jobNodeDetails);
    public JobNode updateJobNodeFull(JobNode jobNode);
    public void deleteJobNodeById(String id);

}
