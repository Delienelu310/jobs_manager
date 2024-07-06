package com.ilumusecase.jobs_manager.controllers.ilum_controllers.operations_controllers;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.manager.Manager;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroupConfiguraion;
import com.ilumusecase.jobs_manager.schedulers.JobEntityScheduler;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class IlumGroupController {


    @Autowired
    private RepositoryFactory repositoryFactory;

    @Autowired
    private JsonMappersFactory jsonMappersFactory;

    @Autowired
    private Manager manager;

    @Autowired
    private JobEntityScheduler jobEntityScheduler;


    @PostMapping("/projects/{project_id}/job_nodes/{job_node_id}/ilum_group")
    public MappingJacksonValue createIlumGroup(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestBody IlumGroupConfiguraion ilumGroupConfiguraion
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();

        if(jobNode.getJobsQueue().size() == 0){
            throw new RuntimeException("The queue is empty");
        }
        if(jobNode.getTestingJobs().size() == 0){
            throw new RuntimeException("The testing jobs are absent");
        }


        IlumGroup ilumGroup = new IlumGroup();
        ilumGroup.setIlumGroupConfiguraion(ilumGroupConfiguraion);
        ilumGroup.setJobs(jobNode.getJobsQueue());
        ilumGroup.setTestingJobs(jobNode.getTestingJobs());
        ilumGroup.setJobNode(jobNode);
        ilumGroup.setProject(project);


        String groupId = manager.createGroup(ilumGroup);
        ilumGroup.setIlumId(groupId);
        ilumGroup = repositoryFactory.getIlumGroupRepository().updageGroupFull(ilumGroup);


        jobNode.getIlumGroups().add(ilumGroup);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    

        return jsonMappersFactory.getIlumGroupMapper().mapSimpleIlumGroup(ilumGroup);
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/ilum_groups/{ilum_group_id}/start")
    public void startIlumGroup(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @PathVariable("ilum_group_id") String ilumGroupId
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        IlumGroup ilumGroup = repositoryFactory.getIlumGroupRepository().retrieveByIlumId(ilumGroupId);
        if(!projectId.equals(jobNode.getProject().getId())) throw new RuntimeException();
        if(!jobNodeId.equals(ilumGroup.getJobNode().getId())) throw new RuntimeException();

        //initial ilum group config:
        ilumGroup.setCurrentIndex(0);
        ilumGroup.setCurrentTestingIndex(0);
        ilumGroup.setCurrentStartTime(LocalDateTime.now());
        ilumGroup.setCurrentJob(ilumGroup.getJobs().get(0));
        ilumGroup.setMod("NORMAL");



        manager.submitJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()), new HashMap<>());
        try{
            jobEntityScheduler.startIlumGroupLifecycle(ilumGroup);
        }catch(Exception e){
            throw new RuntimeException();
        }
        
    }


    // @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/ilum_groups/{ilum_group_id}/stop")
    // public void stopIlumGroup(
    //     @ProjectId @PathVariable("project_id") String projectId,
    //     @JobNodeId @PathVariable("job_node_id") String jobNodeId
    // ){
    //     JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
    //     if(jobNode.getCurrentGroup() == null){
    //         throw new RuntimeException();
    //     }
    //     IlumGroup ilumGroup = jobNode.getCurrentGroup();


    //     manager.stopJob(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));

    //     try{
    //         // jobEntityScheduler.deleteGroupStatusCheckScheduler(ilumGroup);
    //         // jobEntityScheduler.deleteJobEntityStop(ilumGroup.getJobs().get(ilumGroup.getCurrentIndex()));
    //     }catch(Exception e){
    //         throw new RuntimeException();
    //     }

    //     for(int i = 0; i < ilumGroup.getCurrentIndex(); i++){
    //         JobEntity jobEntity = ilumGroup.getJobs().get(i);
    //         jobNode.getJobsQueue().removeIf(jb -> jb.equals(jobEntity));
    //     }

    //     repositoryFactory.getIlumGroupRepository().deleteById(ilumGroup.getId());

    //     jobNode.setCurrentGroup(null);
    //     repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    // }
}
