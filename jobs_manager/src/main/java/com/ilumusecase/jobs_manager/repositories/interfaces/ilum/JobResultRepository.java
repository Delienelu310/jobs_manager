package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;

import java.util.List;
import java.util.Optional;

import com.ilumusecase.jobs_manager.resources.ilum.JobResult;

public interface JobResultRepository {

    public List<JobResult> retrieveAll();

    public Optional<JobResult> retrieveByIlumId(String ilumId);
    public void deleteJobResultById(String id);
    public String updateJobResultFull(JobResult jobResult );
}
