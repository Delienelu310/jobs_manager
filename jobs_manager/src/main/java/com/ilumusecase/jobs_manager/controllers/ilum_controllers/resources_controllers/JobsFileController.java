package com.ilumusecase.jobs_manager.controllers.ilum_controllers.resources_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFileDetails;
import com.ilumusecase.jobs_manager.s3clients.S3ClientFactory;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.constraints.Min;

@RestController
public class JobsFileController {
    @Autowired
    private FilesValidatorFactory filesValidatorFactory;
    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private S3ClientFactory s3ClientFactory;


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_files")
    @JsonMapperRequest(resource = "JobsFile", type = "simple")
    public Object retrieveJobsFilesOfJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,

        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "extension", defaultValue = "", required = false) String extension,
        @RequestParam(name = "classname", defaultValue = "", required = false) String className,
        @RequestParam(name = "publisher", defaultValue = "", required = false) String publisher,
        @RequestParam(name = "pageSize", defaultValue = "10", required = false) @Min(1) Integer pageSize,
        @RequestParam(name = "pageNumber", defaultValue = "0", required = false) @Min(0) Integer pageNumber


    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        if( ! projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();


        return repositoryFactory.getJobsFileRepositoryInterface()
            .retrieveJobsFilesOfJobNode(jobNodeId, query, extension, className, publisher, pageSize, pageNumber);
    }


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_files/count")
    public long retrieveJobsFilesOfJobNodeCount(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,

        @RequestParam(name = "query", defaultValue = "", required = false) String query,
        @RequestParam(name = "extension", defaultValue = "", required = false) String extension,
        @RequestParam(name = "classname", defaultValue = "", required = false) String className,
        @RequestParam(name = "publisher", defaultValue = "", required = false) String publisher
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        if( ! projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();

        return repositoryFactory.getJobsFileRepositoryInterface().countJobsFilesOfJobNode(jobNodeId, query, extension, className, publisher);
    }



    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_files/{jobs_file_id}")
    @JsonMapperRequest(resource = "JobsFile", type = "simple")
    public Object retrieveJobsFileById(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("jobs_file_id") String jobsFileId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId);

        if( ! projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
        if( ! jobNodeId.equals(jobsFile.getJobNode().getId())) throw new RuntimeException(); 

        return jobsFile;
    }


    @PostMapping(value = "/projects/{project_id}/job_nodes/{job_node_id}/jobs_files", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String  uploadJobsFile(
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

    
        return jobsFile.getId();

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
