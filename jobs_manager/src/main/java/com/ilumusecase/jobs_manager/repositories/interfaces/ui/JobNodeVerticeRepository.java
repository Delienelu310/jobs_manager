package com.ilumusecase.jobs_manager.repositories.interfaces.ui;

import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.ui.JobNodeVertice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface JobNodeVerticeRepository {

    public Optional<JobNodeVertice> retrieveByJobNodeId(String jobNodeId);
    public Optional<JobNodeVertice> retrieveById( String id);
    public JobNodeVertice updateJobNodeVertice(@Valid @NotNull JobNodeVertice jobNodeVertice);
} 
