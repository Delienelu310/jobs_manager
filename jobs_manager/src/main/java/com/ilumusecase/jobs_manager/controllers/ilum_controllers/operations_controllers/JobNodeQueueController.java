package com.ilumusecase.jobs_manager.controllers.ilum_controllers.operations_controllers;


import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntityDetails;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.constraints.Min;

@RestController
public class JobNodeQueueController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;

    private boolean checkJarsCompatibility(JobNode jobNode, JobScript jobScript){
        
        
        outer: for(JobsFile jobsFile : jobScript.getJobsFiles()){
            for(String fullClassName : jobsFile.getAllClasses()){
                
                for(JobEntity usedJobEntity : jobNode.getJobsQueue()){
                    if(!jobScript.getExtension().equals(usedJobEntity.getJobScript().getExtension())){
                        continue;
                    }
                    if(jobScript.getJobsFiles().contains(jobsFile)){
                        continue outer;
                    }

                    for(JobsFile usedJobsFile : usedJobEntity.getJobScript().getJobsFiles()){
                        
                        if(usedJobsFile.getAllClasses().contains(fullClassName)) return false;
                    }

                }

            }
        }

        outer: for(JobsFile jobsFile : jobScript.getJobsFiles()){
            for(String fullClassName : jobsFile.getAllClasses()){
                
                for(JobEntity usedJobEntity : jobNode.getTestingJobs()){
                    if(!jobScript.getExtension().equals(usedJobEntity.getJobScript().getExtension())){
                        continue;
                    }
                    if(jobScript.getJobsFiles().contains(jobsFile)){
                        continue outer;
                    }

                    for(JobsFile usedJobsFile : usedJobEntity.getJobScript().getJobsFiles()){
                        if(usedJobsFile.getAllClasses().contains(fullClassName)) return false;
                    }

                }

            }
        }


        return true;
    }
    
    
    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/queue/{queue_type}")
    @JsonMapperRequest(resource = "JobEntity", type="simple")
    @AuthorizeJobRoles
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public Object retrieveQueue(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("queue_type") String queueType,
        @RequestParam(name="author", required = false, defaultValue = "") String author,
        @RequestParam(name="query", required = false, defaultValue = "") String query,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber        
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        if(!jobNode.getProject().getId().equals(projectId)) throw new RuntimeException();


        return repositoryFactory.getJobRepository().retrieveQueue(jobNodeId, queueType, query, author, pageSize, pageNumber);
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/queue/{queue_type}/count")
    @AuthorizeJobRoles
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public long retrieveQueueCount(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("queue_type") String queueType,
        @RequestParam(name="author", required = false, defaultValue = "") String author,
        @RequestParam(name="query", required = false, defaultValue = "") String query      
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        if(!jobNode.getProject().getId().equals(projectId)) throw new RuntimeException();


        return repositoryFactory.getJobRepository().retrieveQueueCount(jobNodeId, queueType, query, author);
    }


    private record JobEntityPost(JobEntityDetails details, String configuration){

    }

    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/{queue_type}/job_entities/{job_script_id}")
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER})
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public String addJobEntityToQueue(
        Authentication authentication,
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_script_id") String jobScriptId,
        @PathVariable("queue_type") String queueType,
        @RequestBody JobEntityPost jobEntityPost
    ){

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        JobScript jobScript = repositoryFactory.getJobScriptRepository().retrieveJobScriptById(jobScriptId).orElseThrow(RuntimeException::new);
        AppUser author = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());


        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
        if(!jobScript.getJobNode().getId().equals(jobNodeId)) throw new RuntimeException();

        if(!checkJarsCompatibility(jobNode, jobScript)) throw new RuntimeException();

        JobEntity jobEntity = new JobEntity();
        jobEntity.setConfiguration(jobEntityPost.configuration());
        jobEntity.setJobEntityDetails(jobEntityPost.details());
        jobEntity.setJobScript(jobScript);
        jobEntity.setProject(project);
        jobEntity.setJobNode(jobNode);
        jobEntity.setAuthor(author);

        jobEntity = repositoryFactory.getJobRepository().updateJobFull(jobEntity);

        if(queueType.equals("jobsQueue")){
            jobNode.getJobsQueue().add(jobEntity);
        }else if(queueType.equals("testingJobs")){
            jobNode.getTestingJobs().add(jobEntity);
        }else {
            throw new RuntimeException();
        }
        
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        return jobEntity.getId();
    }


    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/{queue_type}/job_entities/{job_entity_id}")
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER, JobNodePrivilege.SCRIPTER, JobNodePrivilege.TESTER})
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public void removeJobEntityFromQueue(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_entity_id") String jobEntityId,
        @PathVariable("queue_type") String queueType
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
        JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobEntityId);
        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
        if(!jobNodeId.equals(jobEntity.getJobNode().getId())) throw new RuntimeException();


        if(queueType.equals("jobsQueue")){
            if(jobNode.getJobsQueue().contains(jobEntity)){
                jobNode.getJobsQueue().remove(jobEntity);
            }else{
               throw new RuntimeException();
            }
    
        }else if(queueType.equals("testingJobs")){
            if(jobNode.getTestingJobs().contains(jobEntity)){
                jobNode.getTestingJobs().remove(jobEntity);
            }else{
                throw new RuntimeException();
            }
    
        }else{
            throw new RuntimeException("Invalid job_queue path variable value");
        }

        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
        repositoryFactory.getJobRepository().deleteJob(jobEntityId);

    }

}
