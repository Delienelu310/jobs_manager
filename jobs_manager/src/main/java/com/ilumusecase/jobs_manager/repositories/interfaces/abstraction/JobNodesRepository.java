package com.ilumusecase.jobs_manager.repositories.interfaces.abstraction;

import java.util.List;

import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNodeDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

public interface JobNodesRepository{
    
    public List<JobNode> retrieveByProjectId(String projectId);
    public JobNode retrieveById(String id);
    public List<JobNode> retrieveAll();


    public JobNode createJobNode(Project project, JobNodeDetails jobNodeDetails);
    public JobNode updateJobNode(String id, JobNodeDetails jobNodeDetails);
    public JobNode updateJobNodeFull(JobNode jobNode);
    public void deleteJobNodeById(String id);

}
