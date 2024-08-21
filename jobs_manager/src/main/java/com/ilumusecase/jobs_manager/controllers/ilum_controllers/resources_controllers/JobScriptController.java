package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import java.util.LinkedList;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.exceptions.GeneralResponseException;
import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;
import com.ilumusecase.jobs_manager.resources.ilum.JobScriptDetails;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.annotations.JobScriptId;
import com.ilumusecase.jobs_manager.validation.resource_inheritance.annotations.JobsFileId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
public class JobScriptController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts")
    @JsonMapperRequest(type="simple", resource = "JobScript")
    @AuthorizeJobRoles
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public Object retrieveJobScriptsForJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "publisher", defaultValue = "", required = false) String publisher,
        @RequestParam(name = "extension", defaultValue = "", required = false) String extension,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber        
    ){
        return repositoryFactory.getJobScriptRepository().retrieveJobScriptsOfJobNode(jobNodeId, query, extension, publisher, pageSize, pageNumber);
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/count")
    @AuthorizeJobRoles
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public long retrieveJobScriptsForJobNodeCount(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "publisher", defaultValue = "", required = false) String publisher,
        @RequestParam(name = "extension", defaultValue = "", required = false) String extension  
    ){
        return repositoryFactory.getJobScriptRepository().countJobScriptsOfJobNode(jobNodeId, query, extension, publisher);
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}")
    @JsonMapperRequest(type="simple", resource = "JobScript")
    @AuthorizeJobRoles
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public Object retrieveJobScriptById(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @JobScriptId @PathVariable("job_script_id") String jobScriptId
    ){
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(() ->
            new ResourceNotFoundException(JobScript.class.getSimpleName(), jobScriptId));
      
        return jobScript;
    }


    private record JobScriptDTO(
        @Valid JobScriptDetails jobScriptDetails,
        @NotBlank  @Size(min = 3, max = 100)String classFullName,
        @NotBlank String extension
    ){}
    
    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts")
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER})
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public String createJobScript(
        Authentication authentication,
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestBody @Valid @NotNull JobScriptDTO jobScriptDTO
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());

       
        JobScript jobScript = new JobScript();
        jobScript.setClassFullName(jobScriptDTO.classFullName());
        jobScript.setExtension(jobScriptDTO.extension());
        jobScript.setJobScriptDetails(jobScriptDTO.jobScriptDetails());

        jobScript.setJobsFiles(new LinkedList<>());
        jobScript.setAuthor(appUser);
        jobScript.setProject(project);
        jobScript.setJobNode(jobNode);

        jobScript = repositoryFactory.getJobScriptRepository().updateFullJobScript(jobScript);

        jobNode.getJobScripts().add(jobScript);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        return jobScript.getId();
        
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}/jobs_files/{jobs_file_id}")
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER})
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public void addJobScriptJobsFile(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @JobsFileId @PathVariable("jobs_file_id") String jobsFileId,
        @JobScriptId @PathVariable("job_script_id") String jobScriptId
    ){

        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId)
            .orElseThrow(() -> new ResourceNotFoundException(JobsFile.class.getSimpleName(), jobsFileId));
        
        if( ! jobScript.getExtension().equals(jobsFile.getExtension())){
            throw new GeneralResponseException("The extensions of job script and jobs file must be equal");
        }
        jobScript.getJobsFiles().add(jobsFile);
   

        repositoryFactory.getJobScriptRepository().updateFullJobScript(jobScript);

    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}/jobs_files/{jobs_file_id}")
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER})
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public void removeJobScriptJobsFile(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @JobsFileId @PathVariable("jobs_file_id") String jobsFileId,
        @JobScriptId @PathVariable("job_script_id") String jobScriptId
    ){
      
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId)
            .orElseThrow(() -> new ResourceNotFoundException(JobsFile.class.getSimpleName(), jobsFileId));

        if(!jobScript.getJobsFiles().contains(jobsFile)){
            throw new GeneralResponseException("The jobsFile does not belong to the job script");
        }
        jobScript.getJobsFiles().remove(jobsFile);

        repositoryFactory.getJobScriptRepository().updateFullJobScript(jobScript);
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}/job_script_details")
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER})
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public void updateJobScriptDetails(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @JobScriptId @PathVariable("job_script_id") String jobScriptId,
        @RequestBody @Valid @NotNull JobScriptDetails jobScriptDetails
    ){ 
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
      
        jobScript.setJobScriptDetails(jobScriptDetails);

        repositoryFactory.getJobScriptRepository().updateFullJobScript(jobScript);
    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}")
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER})
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public void deleteJobScript(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @JobScriptId @PathVariable("job_script_id") String jobScriptId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(() -> 
            new ResourceNotFoundException(JobScript.class.getSimpleName(), jobScriptId));
 
        if(!jobNode.getJobScripts().contains(jobScript)){
            throw new GeneralResponseException("Job Script does not belong to the job node");
        }
        jobNode.getJobScripts().remove(jobScript);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        repositoryFactory.getJobScriptRepository().deleteJobScript(jobScriptId);
    }


}
