package com.ilumusecase.jobs_manager.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.files_validators.FilesValidatorFactory;
import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.AppUser;
import com.ilumusecase.jobs_manager.resources.JobDetails;
import com.ilumusecase.jobs_manager.resources.JobEntity;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.s3clients.S3ClientFactory;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.IgnoreAuthAspect;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class JobController {

    @Autowired
    private FilesValidatorFactory filesValidatorFactory;
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;
    @Autowired
    private S3ClientFactory s3ClientFactory;
    @Autowired
    private Manager manager;


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs/{job_id}")
    public MappingJacksonValue retrieveJobEntity(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_id") String jobId
    ){
        // check if jobnode if of project id and if job is of jobnode id
        
        
        return jsonMappersFactory.getJobEntityMapper().getFullJobEntity(
            repositoryFactory.getJobRepository().retrieveJobEntity(jobId)
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs")
    public MappingJacksonValue retrieveJobEntitiesOfJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        //check if jobnoe if of project id

        return jsonMappersFactory.getJobEntityMapper().getFullJobEntityList(
            repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId).getJobsQueue()
        );
    }

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);

    @PostMapping(value = "/projects/{project_id}/job_nodes/{job_node_id}/jobs", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public MappingJacksonValue uploadJob(
        Authentication authentication,
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam("files") MultipartFile file,
        @RequestParam("name") String name,
        @RequestParam("description") String description
        
    ){
        
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());


        // check if file has required class 
        String filename = file.getOriginalFilename();
        if(filename == null || filename.lastIndexOf(".") == -1) throw new RuntimeException();
        String extenstion = filename.substring(filename.lastIndexOf(".") + 1);
        
        Optional<String> classPath = filesValidatorFactory
            .getValidator(extenstion).orElseThrow(RuntimeException::new)
            .validate(file, "Main_" + appUser.getUsername() + 
                "_" + appUser.getAppUserDetails().getJobCreatedCounter());

        //save the job entiity
        
        JobDetails jobDetails = new JobDetails();
        jobDetails.setName(name);
        jobDetails.setDescription(description);

        JobEntity jobEntity = new JobEntity();
        jobEntity.setClassPath(classPath.orElseThrow(RuntimeException::new));
        jobEntity.setExtension(extenstion);
        jobEntity.setJobDetails(jobDetails);
        jobEntity.setProject(project);
        jobEntity.setJobNode(jobNode);
        jobEntity.setAuthor(appUser);

        jobEntity = repositoryFactory.getJobRepository().updateJobFull(jobEntity);

        //add job entity to queue 
        jobNode.getJobsQueue().add(jobEntity);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        //send file to s3
        s3ClientFactory.getJobS3Client().uploadJob(jobEntity, file);

        appUser.getAppUserDetails().setJobCreatedCounter(appUser.getAppUserDetails().getJobCreatedCounter() + 1);
        repositoryFactory.getUserDetailsManager().saveAppUser(appUser);

        return jsonMappersFactory.getJobEntityMapper().getFullJobEntity(jobEntity);
        
        
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/job/run_next")
    public void runNextJob(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobEntity jobEntity = jobNode.getJobsQueue().remove(0);
        jobNode.setCurrentJob(jobEntity);
        String ilumId = manager.submitJob(jobNode.getCurrentJob());
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    
        jobEntity.setIlumId(ilumId);
        repositoryFactory.getJobRepository().updateJobFull(jobEntity);
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/job/stop_current")
    public void stopCurrentJob(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        manager.stopJob(jobNode.getCurrentJob());
        jobNode.setCurrentJob(null);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    
    }


    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs/{job_id}")
    public void deleteJob(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_id") String jobId
    ){
        //...
    }



    
}
