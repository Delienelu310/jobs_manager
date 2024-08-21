package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.ilum.JobResult;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers.JobResultsController.IlumGroupData;


@Validated
public interface JobResultRepository {



    public List<JobResult> retrieveAll();
    public void clear();

    public Optional<JobResult> retrieveById(String id);
    public Optional<JobResult> retrieveByIlumId(String ilumId);

    public void deleteJobResultById(String id);
    public String updateJobResultFull(@Valid @NotNull JobResult jobResult );

    public List<JobResult> retrieveJobResults(
        String jobNodeId,
        String ilumGroupId,
        String targetNameQuery,

        boolean includeSuccessfull,
        boolean includeJobErrors,
        boolean includeTesterErrors,

        String targetAuthor,
        String targetClass,
        String targetId,

        String testerNameQuery,
        String testerAuthor,
        String testerClass,
        String testerId,

        @Min(0) Long from,
        @Min(0) Long to,

        String sortMetric,
        @Min(1) Integer pageSize,
        @Min(0) Integer pageNumber

    );
    public Long retrieveJobResultsCount(
        String jobNodeId,
        String ilumGroupId,
        String targetNameQuery,

        boolean includeSuccessfull,
        boolean includeJobErrors,
        boolean includeTesterErrors,

        String targetAuthor,
        String targetClass,
        String targetId,

        String testerNameQuery,
        String testerAuthor,
        String testerClass,
        String testerId,

        @Min(0) Long from,
        @Min(0) Long to
    );

    public List<IlumGroupData> retrieveIlumGroupsOfJobResults(
        String jobNodeId,
        String query,
        @Min(0) Long from,
        @Min(0)Long to,
        @Min(1)Integer pageSize,
        @Min(0)Integer pageNumber
    );

    public Long retrieveIlumGroupsOfJobResultsCount(
        String jobNodeId,
        String query,
        @Min(0) Long from,
        @Min(0) Long to
    );


    public List<JobScript> retrieveTestersOfJobResults(
        String jobNodeId,
        String testerQuery,
        String testerAuthor,
        String testerClass,

        String ilumGroupId,

        @Min(0) Long from,
        @Min(0) Long to,
        @Min(1) Integer pageSize,
        @Min(0) Integer pageNumber
    );

    public Long retrieveTesterOfJobResultsCount(
        String jobNodeId,
        String testerQuery,
        String testerAuthor,
        String testerClass,

        String ilumGroupId,

        @Min(0) Long from,
        @Min(0) Long to
    );

    public List<String> retrieveTesterMetrics(
        String jobNodeId,
        String testerId,
        String query,
        String ilumGroupId,
        @Min(1) Integer pageSize,
        @Min(0) Integer pageNumber
    );

    public Long retrieveTesterMetricsCount(
        String jobNodeId,
        String testerId,
        String query,
        String ilumGroupId
    );

}
