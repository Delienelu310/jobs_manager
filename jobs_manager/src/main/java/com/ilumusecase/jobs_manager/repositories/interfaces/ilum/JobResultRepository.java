package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;

import java.util.Optional;

import com.ilumusecase.jobs_manager.resources.ilum.JobResult;

public interface JobResultRepository {

    public Optional<JobResult> retrieveByIlumId(String ilumId);
    public void deleteJobResultById(String id);
    public String updateJobResultFull(JobResult jobResult );
}
