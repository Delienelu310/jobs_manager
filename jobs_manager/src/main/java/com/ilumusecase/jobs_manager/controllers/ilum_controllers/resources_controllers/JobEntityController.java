package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntityDetails;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class JobEntityController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;



    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_entities/{job_entity_id}")
    public MappingJacksonValue getJobEntityById(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_entity_id") String jobEntityId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobEntityId);
        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();
        if(!jobNodeId.equals(jobEntity.getJobNode().getId())) throw new RuntimeException();


        return jsonMappersFactory.getJobEntityMapper().getFullJobEntity(jobEntity);

    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_entities")
    public MappingJacksonValue getJobEntitiesByGroupId(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
      
        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();


        return jsonMappersFactory.getJobEntityMapper().getFullJobEntityList(jobNode.getJobEntities());

    }



    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}/job_entities")
    public MappingJacksonValue createJobEntity(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String jobScriptId,
        @RequestBody JobEntityDetails jobEntityDetails,
        @RequestParam(name = "configuration", required = false, defaultValue = "") String configuration
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
      
        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();
        if(!jobScript.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();


        JobEntity jobEntity = new JobEntity();
        jobEntity.setConfiguration(configuration);
        jobEntity.setJobEntityDetails(jobEntityDetails);
        jobEntity.setJobScript(jobScript);
        jobEntity.setProject(project);
        jobEntity.setJobNode(jobNode);

        jobEntity = repositoryFactory.getJobRepository().updateJobFull(jobEntity);


        jobNode.getJobEntities().add(jobEntity);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        return jsonMappersFactory.getJobEntityMapper().getFullJobEntity(jobEntity);
    }


    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/job_scripts/{job_script_id}/job_entities/{job_entity_id}")
    public void deleteJobEntity(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String jobScriptId,
        @PathVariable("job_entity_id") String jobEntityId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
        JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobEntityId);

        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();
        if(!jobScript.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();
        if(!jobScriptId.equals(jobEntity.getJobScript().getId())) throw new RuntimeException();



        if(jobNode.getJobsQueue().contains(jobEntity) || jobNode.getTestingJobs().contains(jobEntity)){
            throw new RuntimeException();
        }
        jobNode.getJobEntities().remove(jobEntity);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);


        repositoryFactory.getJobRepository().deleteJob(jobEntityId);

    }


}
