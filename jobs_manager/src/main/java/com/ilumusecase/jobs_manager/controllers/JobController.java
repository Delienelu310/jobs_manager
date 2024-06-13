package com.ilumusecase.jobs_manager.controllers;

import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
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
import com.ilumusecase.jobs_manager.resources.IlumGroup;
import com.ilumusecase.jobs_manager.resources.JobDetails;
import com.ilumusecase.jobs_manager.resources.JobEntity;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.JobsFile;
import com.ilumusecase.jobs_manager.resources.Project;
import com.ilumusecase.jobs_manager.s3clients.S3ClientFactory;
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
        @RequestParam("description") String description,
        @RequestParam("job_classes") String jobClasses
    ){
        
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());

        if(!project.getId().equals(jobNode.getId())) throw new RuntimeException();


        List<String> jobClassesList = Stream.of(jobClasses.split(",")).toList();
        
        // check if file has required class 
        String filename = file.getOriginalFilename();
        if(filename == null || filename.lastIndexOf(".") == -1) throw new RuntimeException();
        String extenstion = filename.substring(filename.lastIndexOf(".") + 1);
        
        if(!filesValidatorFactory
            .getValidator(extenstion).orElseThrow(RuntimeException::new)
            .validate(file, jobNode, jobClassesList)
        ) throw new RuntimeException();

        //save the job entiity
        
        JobDetails jobDetails = new JobDetails();
        jobDetails.setName(name);
        jobDetails.setDescription(description);

        JobsFile jobsFile = new JobsFile();
        jobsFile.setJobDetails(jobDetails);
        jobsFile.setExtension(extenstion);
        jobsFile.setAuthor(appUser);
        jobsFile.setJobNode(jobNode);
        jobsFile.setJobClassesPaths(jobClassesList);
        jobsFile.setAllClasses(
            filesValidatorFactory.getValidator(extenstion).orElseThrow(RuntimeException::new)
            .retrieveFileClasses(file)
        );

        jobsFile = repositoryFactory.getJobsFileRepositoryInterface().updateJobsFileFull(jobsFile);

        //add jobs file to job node
        jobNode.getJobsFiles().add(jobsFile);
        for(String className : jobsFile.getAllClasses()){
            if(!jobNode.getUsedClassnames().containsKey(className)){
                jobNode.getUsedClassnames().put(className, 1);
            }else{
                jobNode.getUsedClassnames().put(className, jobNode.getUsedClassnames().get(className) + 1);
            }
        }
        for(String className : jobsFile.getJobClassesPaths()){
            jobNode.getJobClasses().add(className);
        }
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        //send file to s3
        s3ClientFactory.getJobS3Client().uploadJob(jobsFile, file);

        repositoryFactory.getUserDetailsManager().saveAppUser(appUser);

        return jsonMappersFactory.getJobsFileJsonMapper().getFullJobsFile(jobsFile);
   
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_queue/remove/{job_id}")
    public void removeJobFromQueue(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_id") String jobId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobEntity jobEntity = repositoryFactory.getJobRepository().retrieveJobEntity(jobId);
        IlumGroup ilumGroup = jobNode.getCurrentGroup();

        //in ilum group
        //if the job is currently running, stop it
        if(jobEntity.equals(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()))){
            manager.stopJob(jobEntity);
        }

        //if the job is in upcomming queue
        for(int i = ilumGroup.getCurrentIndex() + 1; i < ilumGroup.getJobs().size(); i++){
            if(ilumGroup.getJobs().get(i).equals(jobEntity)){
                ilumGroup.getJobs().remove(i);
                i--;
            }
        }
        ilumGroup = repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);

        //inside of job node
        jobNode.getJobsQueue().removeIf(jb -> jb.equals(jobEntity));
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs/{job_id}")
    public void deleteJob(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("job_id") String jobId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobId);

        if(jobNode.getCurrentGroup() != null){
            throw new RuntimeException();
        }

        jobNode.getJobsQueue().removeIf(jb -> jb.getJobsFile().equals(jobsFile));
        jobNode.getJobsFiles().removeIf(jb -> jb.equals(jobsFile));
        for(String className : jobsFile.getAllClasses()){
            if(jobNode.getUsedClassnames().get(className).equals(1)){
                jobNode.getUsedClassnames().remove(className);
            }else{
                jobNode.getUsedClassnames().put(className, jobNode.getUsedClassnames().get(className) + 1);
            }
        }
        for(String className : jobsFile.getJobClassesPaths()){
            jobNode.getJobClasses().remove(className);
        }

        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        s3ClientFactory.getJobS3Client().deleteJob(jobsFile);

        repositoryFactory.getJobRepository().deleteJob(jobId);

        
    }



    
}
