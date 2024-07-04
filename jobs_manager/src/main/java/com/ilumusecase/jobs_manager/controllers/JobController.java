package com.ilumusecase.jobs_manager.controllers;


import java.util.LinkedList;

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
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFileDetails;
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

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_files/{jobs_file_id}")
    public MappingJacksonValue retrieveJobsFileById(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("jobs_file_id") String jobsFileId
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        JobsFile jobsFile = repositoryFactory.getJobsFileRepositoryInterface().retrieveJobsFileById(jobsFileId);


        return jsonMappersFactory.getJobsFileJsonMapper().getFullJobsFile(
            jobsFile
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/jobs_files")
    public MappingJacksonValue retrieveJobsFilesByJobNodeId(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);

        // if(!project.getId().equals(jobNode.getProject().getId())) throw new RuntimeException();

        return jsonMappersFactory.getJobsFileJsonMapper().getFullJobsFilesList(
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
        @RequestParam("jobs_details") JobsFileDetails jobsFileDetails
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
        
        //send file to s3
        s3ClientFactory.getJobS3Client().uploadJob(jobsFile, file);

        //save jobsfile to db
        repositoryFactory.getJobsFileRepositoryInterface().updateJobsFileFull(jobsFile);


        //add jobs file to job node
        jobNode.getJobsFiles().add(jobsFile);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

        return jsonMappersFactory.getJobsFileJsonMapper().getFullJobsFile(jobsFile);

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
    
        jobNode.getJobsFiles().removeIf(jb -> jb.equals(jobsFile));
     
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
        repositoryFactory.getJobRepository().deleteJob(jobsFileId);
        s3ClientFactory.getJobS3Client().deleteJob(jobsFile);

    }



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


}
