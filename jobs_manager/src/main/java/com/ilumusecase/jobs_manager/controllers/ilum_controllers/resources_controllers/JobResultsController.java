package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroupDetails;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;

import java.util.List;
import jakarta.validation.constraints.Min;

@RestController
public class JobResultsController {


    public record IlumGroupData(String ilumGroupId, IlumGroupDetails ilumGroupDetails){

    }
    
    @Autowired
    private RepositoryFactory repositoryFactory;

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results")
    @JsonMapperRequest(type="simple", resource = "JobResult")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public Object retrieveJobResults(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "ilum_group_id", required = false, defaultValue = "") String ilumGroupId,
        @RequestParam(name = "query", defaultValue = "", required = false) String targetNameQuery,
        
        @RequestParam(name = "include_successfull", required = false, defaultValue = "true") boolean includeSuccessfull,
        @RequestParam(name = "include_job_errors", required = false, defaultValue = "false") boolean includeJobErrors,
        @RequestParam(name = "include_tester_errors", required = false, defaultValue = "false") boolean includeTesterErrors,

        @RequestParam(name = "tester_name" , defaultValue = "", required = false) String testerNameQuery,
        @RequestParam(name = "tester_author", defaultValue = "", required = false) String testerAuthor,
        @RequestParam(name = "tester_classname", defaultValue = "", required = false) String testerClass,
        @RequestParam(name = "tester_id", defaultValue = "", required = false) String testertId,

        @RequestParam(name = "target_author", defaultValue = "", required = false) String targetAuthor,
        @RequestParam(name = "target_classname", defaultValue = "", required = false) String targetClass,
        @RequestParam(name = "target_id", defaultValue = "", required = false) String targetId,

        @RequestParam(name = "from", defaultValue = "0", required = false) Long from,
        @RequestParam(name = "to", required = false) Long to,
        
        @RequestParam(name = "sort_metric", defaultValue = "", required = false) String metric,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber  
    ){
        return repositoryFactory.getJobResultRepository().retrieveJobResults(jobNodeId, ilumGroupId, targetNameQuery, 
            includeSuccessfull, includeJobErrors, includeTesterErrors, 
            targetAuthor, targetClass, targetId, 
            testerNameQuery, testerAuthor, testerClass, testertId, 
            from, to, 
            metric, pageSize, pageNumber
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/count")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public Long retrieveJobResultsCount(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "ilum_group_id", required = false, defaultValue = "") String ilumGroupId,
        @RequestParam(name = "query", defaultValue = "", required = false) String targetNameQuery,
        
        @RequestParam(name = "include_successfull", required = false, defaultValue = "true") boolean includeSuccessfull,
        @RequestParam(name = "include_job_errors", required = false, defaultValue = "false") boolean includeJobErrors,
        @RequestParam(name = "include_tester_errors", required = false, defaultValue = "false") boolean includeTesterErrors,

        @RequestParam(name = "tester_name" , defaultValue = "", required = false) String testerNameQuery,
        @RequestParam(name = "tester_author", defaultValue = "", required = false) String testerAuthor,
        @RequestParam(name = "tester_classname", defaultValue = "", required = false) String testerClass,
        @RequestParam(name = "tester_id", defaultValue = "", required = false) String testertId,

        @RequestParam(name = "target_author", defaultValue = "", required = false) String targetAuthor,
        @RequestParam(name = "target_classname", defaultValue = "", required = false) String targetClass,
        @RequestParam(name = "target_id", defaultValue = "", required = false) String targetId,

        @RequestParam(name = "from", defaultValue = "0", required = false) Long from,
        @RequestParam(name = "to", required = false) Long to

    ){
        return repositoryFactory.getJobResultRepository().retrieveJobResultsCount(jobNodeId, ilumGroupId, targetNameQuery, 
            includeSuccessfull, includeJobErrors, includeTesterErrors, 
            targetAuthor, targetClass, targetId,
            testerNameQuery, testerAuthor, testerClass, testertId, 
            from, to
        );
    }



    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/ilum_groups")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public List<IlumGroupData> retrieveIlumGroupsOfJobResults(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "from", defaultValue = "0", required = false) Long from,
        @RequestParam(name = "to", required = false) Long to,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber  
    ){
        return repositoryFactory.getJobResultRepository().retrieveIlumGroupsOfJobResults(jobNodeId, query, from, to, pageSize, pageNumber);
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/ilum_groups/count")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public Long retrieveIlumGroupsOfJobResultsCount(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "from", defaultValue = "0", required = false) Long from,
        @RequestParam(name = "to", required = false) Long to
    ){
        return repositoryFactory.getJobResultRepository().retrieveIlumGroupsOfJobResultsCount(jobNodeId, query, from, to);

    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/job_scripts")
    @JsonMapperRequest(type="simple", resource = "JobScript")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public Object retrieveJobScriptOfJobResults(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,

        @RequestParam(name = "query", defaultValue = "", required = false) String testerNameQuery,
        @RequestParam(name = "tester_author", defaultValue = "", required = false) String testerAuthor,
        @RequestParam(name = "tester_classname", defaultValue = "", required = false) String testerClass,

        @RequestParam(name = "ilum_group_id", defaultValue = "", required = false) String ilumGroupId,

        @RequestParam(name = "from", defaultValue = "0", required = false) Long from,
        @RequestParam(name = "to", required = false) Long to,

        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber  
    ){
        return repositoryFactory.getJobResultRepository().retrieveTestersOfJobResults(
            jobNodeId, testerNameQuery, testerAuthor, testerClass, 
            ilumGroupId, from, to, pageSize, pageNumber
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/job_scripts/count")
    @JsonMapperRequest(type="simple", resource = "JobScript")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public Long retrieveJobScriptOfJobResultsCount(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,

        @RequestParam(name = "query", defaultValue = "", required = false) String testerNameQuery,
        @RequestParam(name = "tester_author", defaultValue = "", required = false) String testerAuthor,
        @RequestParam(name = "tester_classname", defaultValue = "", required = false) String testerClass,

        @RequestParam(name = "ilum_group_id", defaultValue = "", required = false) String ilumGroupId,

        @RequestParam(name = "from", defaultValue = "0", required = false) Long from,
        @RequestParam(name = "to", required = false) Long to
    ){
        return repositoryFactory.getJobResultRepository().retrieveTesterOfJobResultsCount(
            jobNodeId, testerNameQuery, testerAuthor, testerClass, 
            ilumGroupId, from, to
        );
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/job_scripts/{job_script_id}/metrics")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public List<String> retrieveMetricsOfTester(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String testerId,


        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "ilum_group_id", defaultValue = "", required = false) String ilumGroupId,

        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber  
    ){
        return repositoryFactory.getJobResultRepository().retrieveTesterMetrics(jobNodeId, testerId, query, ilumGroupId, pageSize, pageNumber);
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/job_scripts/{job_script_id}/metrics/count")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public Long retrieveMetricsOfTesterCount(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String testerId,

        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "ilum_group_id", defaultValue = "", required = false) String ilumGroupId
    ){
        return repositoryFactory.getJobResultRepository().retrieveTesterMetricsCount(jobNodeId, testerId, query, ilumGroupId);
    }

 
    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/{job_result_id}")
    @JsonMapperRequest(type="simple", resource = "JobResult")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles
    public Object retrieveJobResultById(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_result_id") String jobResultId
    ){
        return repositoryFactory.getJobResultRepository().retrieveById(jobResultId).orElseThrow();
    }


    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_results/{job_result_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER})
    public void deleteResult(
        @PathVariable("project_id") String projectId,
        @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_result_id") String jobResultId
    
    ){
        //todo: check if job result is of job node, and if job node is of project
        //todo: delete the job result also on the ilum core server

        repositoryFactory.getJobResultRepository().deleteJobResultById(jobResultId);
    }

}
