package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;

import java.util.List;
import java.util.Optional;
import com.ilumusecase.jobs_manager.resources.ilum.JobResult;

import com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers.JobResultsController.IlumGroupData;


public interface JobResultRepository {



    public List<JobResult> retrieveAll();
    public void clear();

    public Optional<JobResult> retrieveById(String id);
    public Optional<JobResult> retrieveByIlumId(String ilumId);

    public void deleteJobResultById(String id);
    public String updateJobResultFull(JobResult jobResult );

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

        Long from,
        Long to,

        String sortMetric,
        Integer pageSize,
        Integer pageNumber

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

        Long from,
        Long to
    );

    public List<IlumGroupData> retrieveIlumGroupsOfJobResults(
        String jobNodeId,
        String query,
        Long from,
        Long to,
        Integer pageSize,
        Integer pageNumber
    );

    public Long retrieveIlumGroupsOfJobResultsCount(
        String jobNodeId,
        String query,
        Long from,
        Long to
    );

}
