package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ilumusecase.jobs_manager.files_validators.FilesValidatorFactory;
import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFileDetails;
import com.ilumusecase.jobs_manager.s3clients.S3ClientFactory;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class JobsFileController {
    @Autowired
    private FilesValidatorFactory filesValidatorFactory;
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;
    @Autowired
    private S3ClientFactory s3ClientFactory;

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_files/{jobs_file_id}")
    public MappingJacksonValue retrieveJobsFileById(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("jobs_file_id") String jobsFileId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId);

        if( ! projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
        if( ! jobNodeId.equals(jobsFile.getJobNode().getId())) throw new RuntimeException(); 

        return jsonMappersFactory.getJobsFileJsonMapper().getSimpleJobsFile(
            jobsFile
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_files")
    public MappingJacksonValue retrieveJobsFilesByJobNodeId(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){

        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( ! projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();

        return jsonMappersFactory.getJobsFileJsonMapper().getSimpleJobsFilesList(
            repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFilesByJobNodeId(jobNodeId)
        );
    }

    @PostMapping(value = "/projects/{project_id}/job_nodes/{job_node_id}/jobs_files", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public MappingJacksonValue uploadJobsFile(
        Authentication authentication,
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestParam("files") MultipartFile file,
        @RequestParam("extension") String extension,
        @RequestPart("jobs_details") JobsFileDetails jobsFileDetails
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        AppUser appUser = repositoryFactory.getUserDetailsManager().findByUsername(authentication.getName());

        // if(!project.getId().equals(jobNode.getId())) throw new RuntimeException();


        //set the fields of jobs_file objects
        JobsFile jobsFile = new JobsFile();
        jobsFile.setJobDetails(jobsFileDetails);
        jobsFile.setExtension(extension);
        jobsFile.setAllClasses(
            filesValidatorFactory.getValidator(jobsFile.getExtension())
                .orElseThrow(RuntimeException::new)
                .retrieveFileClasses(file)
        );
        jobsFile.setPublisher(appUser);
        jobsFile.setProject(project);
        jobsFile.setJobNode(jobNode);


        //save jobsfile to db
        repositoryFactory.getJobsFileRepositoryInterface().updateJobsFileFull(jobsFile);


        //add jobs file to job node
        jobNode.getJobsFiles().add(jobsFile);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);


        //send file to s3
        s3ClientFactory.getJobS3Client().uploadJob(jobsFile, file);

    
        return jsonMappersFactory.getJobsFileJsonMapper().getSimpleJobsFile(jobsFile);

    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_files/{jobs_file_id}")
    public void deleteJobsFile(
        Authentication authentication,
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("jobs_file_id") String jobsFileId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId);


        if( ! repositoryFactory.getJobScriptRepository().retrieveJobScriptsByJobsFileId(jobsFileId).isEmpty()){
            throw new RuntimeException("The job scripts are now using this jar");
        }
    
        jobNode.getJobsFiles().remove(jobsFile);
     
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
        repositoryFactory.getJobsFileRepositoryInterface().deleteJobsFileById(jobsFileId);
        s3ClientFactory.getJobS3Client().deleteJob(jobsFile);

    }

}
