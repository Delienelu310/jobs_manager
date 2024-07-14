package com.ilumusecase.jobs_manager.repositories.interfaces;

import java.util.Optional;

import com.ilumusecase.jobs_manager.resources.ui.JobNodeVertice;

public interface JobNodeVerticeRepository {

    public Optional<JobNodeVertice> retrieveById( String id);
    public JobNodeVertice updateJobNodeVertice(JobNodeVertice jobNodeVertice);
} 
