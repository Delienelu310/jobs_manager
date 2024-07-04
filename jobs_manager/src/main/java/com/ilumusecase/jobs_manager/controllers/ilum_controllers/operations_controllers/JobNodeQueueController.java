package com.ilumusecase.jobs_manager.controllers.ilum_controllers.operations_controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class JobNodeQueueController {
    
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;

    private boolean checkJarsCompatibility(JobNode jobNode, JobEntity jobEntity){
        
        
        for(JobsFile jobsFile : jobEntity.getJobScript().getJobsFiles()){
            for(String fullClassName : jobsFile.getAllClasses()){
                
                for(JobEntity usedJobEntity : jobNode.getJobsQueue()){
                    if(!jobEntity.getJobScript().getExtension().equals(usedJobEntity.getJobScript().getExtension())){
                        continue;
                    }

                    for(JobsFile usedJobsFile : usedJobEntity.getJobScript().getJobsFiles()){
                        if(usedJobsFile.getAllClasses().contains(fullClassName)) return false;
                    }

                }

            }
        }

        for(JobsFile jobsFile : jobEntity.getJobScript().getJobsFiles()){
            for(String fullClassName : jobsFile.getAllClasses()){
                
                for(JobEntity usedJobEntity : jobNode.getTestingJobs()){
                    if(!jobEntity.getJobScript().getExtension().equals(usedJobEntity.getJobScript().getExtension())){
                        continue;
                    }

                    for(JobsFile usedJobsFile : usedJobEntity.getJobScript().getJobsFiles()){
                        if(usedJobsFile.getAllClasses().contains(fullClassName)) return false;
                    }

                }

            }
        }


        return true;
    }
    
    
    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/toggle/{job_queue}/{job_entity_id}")
    public MappingJacksonValue toggleJobEntityQueue(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_entity_id") String jobEntityId,
        @PathVariable("job_queue") String jobQueue
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobEntityId);
        if(!projectId.equals(jobNode.getId())) throw new RuntimeException();
        if(!jobNodeId.equals(jobEntity.getJobNode().getId())) throw new RuntimeException();


        if(jobQueue.equals("job_queue")){
            if(jobNode.getJobEntities().contains(jobEntity)){
                jobNode.getJobsQueue().remove(jobEntity);
            }else{
                if(!checkJarsCompatibility(jobNode, jobEntity)) throw new RuntimeException("The job entity is not compatible with current queue");
    
                jobNode.getJobsQueue().add(jobEntity);
            }
    
        }else if(jobQueue.equals("test_queue")){
            if(jobNode.getTestingJobs().contains(jobEntity)){
                jobNode.getTestingJobs().remove(jobEntity);
            }else{
                if(!checkJarsCompatibility(jobNode, jobEntity)) throw new RuntimeException("The job entity is not compatible with current queue");
    
                jobNode.getTestingJobs().add(jobEntity);
            }
    
        }else{
            throw new RuntimeException("Invalid job_queue path variable value");
        }

        

        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode(
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode)
        );

    }
}
