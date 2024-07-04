package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class JobScriptController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts")
    public MappingJacksonValue retrieveJobScriptsByJobNodeId(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();
      
        return jsonMappersFactory.getJobScriptMapper().mapJobScriptListFull(jobNode.getJobScripts());
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}")
    public MappingJacksonValue retrieveJobScriptById(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String jobScriptId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
      
        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();
        if(!jobScript.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();

        return jsonMappersFactory.getJobScriptMapper().mapJobScriptFull(jobScript);
    }
    
    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts")
    public MappingJacksonValue createJobScript(
        Authentication authentication,
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        JobScript jobScript
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());

        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();

        jobScript.setJobsFiles(new LinkedList<>());
        jobScript.setAuthor(appUser);
        jobScript.setProject(project);
        jobScript.setJobNode(jobNode);

        jobScript = repositoryFactory.getJobScriptRepository().updateFullJobScript(jobScript);

        jobNode.getJobScripts().add(jobScript);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        return jsonMappersFactory.getJobScriptMapper().mapJobScriptFull(
            jobScript
        );
        
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}/toggle/jobs_files/{jobs_file_id}")
    public MappingJacksonValue toggleJobScriptJobsFile(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("jobs_file_id") String jobsFileId,
        @PathVariable("job_script_id") String jobScriptId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId);

        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();
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


        return jsonMappersFactory.getJobScriptMapper().mapJobScriptFull(
            repositoryFactory.getJobScriptRepository().updateFullJobScript(jobScript)
        );
    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}")
    public void deleteJobScript(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String jobScriptId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
      
        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();
        if(!jobScript.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();

        jobNode.getJobScripts().remove(jobScript);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        repositoryFactory.getJobScriptRepository().deleteJobScript(jobScriptId);
    }


}
