package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import java.util.LinkedList;

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

import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.constraints.Min;

@RestController
public class JobScriptController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts")
    @JsonMapperRequest(type="simple", resource = "JobScript")
    public Object retrieveJobScriptsForJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "publisher", defaultValue = "", required = false) String publisher,
        @RequestParam(name = "extension", defaultValue = "", required = false) String extension,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber        
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
    
        return repositoryFactory.getJobScriptRepository().retrieveJobScriptsOfJobNode(jobNodeId, query, extension, publisher, pageSize, pageNumber);
    
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/count")
    public long retrieveJobScriptsForJobNodeCount(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "publisher", defaultValue = "", required = false) String publisher,
        @RequestParam(name = "extension", defaultValue = "", required = false) String extension  
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
    
        return repositoryFactory.getJobScriptRepository().countJobScriptsOfJobNode(jobNodeId, query, extension, publisher);
    
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}")
    @JsonMapperRequest(type="simple", resource = "JobScript")
    public Object retrieveJobScriptById(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String jobScriptId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
      
        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
        if(!jobScript.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();

        return jobScript;
    }
    
    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts")
    public String createJobScript(
        Authentication authentication,
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestBody JobScript jobScript
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());

        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();

        jobScript.setJobsFiles(new LinkedList<>());
        jobScript.setAuthor(appUser);
        jobScript.setProject(project);
        jobScript.setJobNode(jobNode);

        jobScript = repositoryFactory.getJobScriptRepository().updateFullJobScript(jobScript);

        jobNode.getJobScripts().add(jobScript);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        return jobScript.getId();
        
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}/toggle/jobs_files/{jobs_file_id}")
    public void toggleJobScriptJobsFile(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("jobs_file_id") String jobsFileId,
        @PathVariable("job_script_id") String jobScriptId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId);

        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
        if(!jobsFile.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();
        if(!jobScript.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();

        if(jobScript.getJobsFiles().contains(jobsFile)){
            jobScript.getJobsFiles().remove(jobsFile);
        }else{
            if( ! jobScript.getExtension().equals(jobsFile.getExtension())){
                throw new RuntimeException();
            }
            jobScript.getJobsFiles().add(jobsFile);
        }

        repositoryFactory.getJobScriptRepository().updateFullJobScript(jobScript);

    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}")
    public void deleteJobScript(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String jobScriptId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
      
        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
        if(!jobScript.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();

        jobNode.getJobScripts().remove(jobScript);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        repositoryFactory.getJobScriptRepository().deleteJobScript(jobScriptId);
    }


}
