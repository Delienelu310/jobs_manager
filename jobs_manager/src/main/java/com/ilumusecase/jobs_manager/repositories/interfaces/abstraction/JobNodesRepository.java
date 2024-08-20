package com.ilumusecase.jobs_manager.repositories.interfaces.abstraction;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNodeDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface JobNodesRepository{
    
    public List<JobNode> retrieveByProjectId(String projectId);
    public Optional<JobNode> retrieveById(String id);
    public List<JobNode> retrieveAll();


    public String createJobNode(Project project, @Valid @NotNull JobNodeDetails jobNodeDetails);
    public void updateJobNode(String id, @Valid @NotNull JobNodeDetails jobNodeDetails);
    public void updateJobNodeFull(@NotNull @Valid JobNode jobNode);
    public void deleteJobNodeById(String id);

}
