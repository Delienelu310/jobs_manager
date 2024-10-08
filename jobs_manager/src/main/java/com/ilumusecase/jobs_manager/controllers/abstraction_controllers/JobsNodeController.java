package com.ilumusecase.jobs_manager.controllers.abstraction_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.exceptions.GeneralResponseException;
import com.ilumusecase.jobs_manager.exceptions.ResourceNotFoundException;
import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.Channel;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelList;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNodeDetails;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeJobRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@RestController
@Validated
public class JobsNodeController {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @Autowired
    private ChannelController channelController;


    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/full")
    @JsonMapperRequest(type="full", resource = "JobNode")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    @AuthorizeJobRoles
    public Object retrieveByIdFull(
        @ProjectId @PathVariable("project_id") String projectId, 
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        return repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId).orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
    
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}/ilum")
    @JsonMapperRequest(type="ilumGroup", resource = "JobNode")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR})
    @AuthorizeJobRoles
    public Object retrieveByIdWithIlumGroup(
        @ProjectId @PathVariable("project_id") String projectId, 
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){
        return repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId).orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
    }


    @GetMapping("/projects/{project_id}/job_nodes/full")
    @JsonMapperRequest(type="full", resource = "JobNode")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.VIEWER, ProjectPrivilege.SCRIPTER})
    public Object retrieveJobNodesByProjectId(@ProjectId @PathVariable("project_id") String projectId){

        return repositoryFactory.getJobNodesRepository().retrieveByProjectId(projectId);
    }

    @PostMapping("/projects/{project_id}/job_nodes")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public String createJobNode(
        @ProjectId @PathVariable("project_id") String projectId, 
        @RequestBody @Valid @NotNull JobNodeDetails jobNodeDetails
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
    
        String jobNodeId = repositoryFactory.getJobNodesRepository().createJobNode( project, jobNodeDetails);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId).orElseThrow(() -> new RuntimeException("Problem with database"));

        
        project.getJobNodes().add(jobNode);
        repositoryFactory.getProjectRepository().updateProjectFull(project);

        return jobNodeId;
        
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER})
    public void updateJobNodeDetails(
        @ProjectId @PathVariable("project_id") String projectId, 
        @JobNodeId @PathVariable("job_node_id") String jobNodeId, 
        @RequestBody @Valid @NotNull JobNodeDetails jobNodeDetails
    ){
       
        repositoryFactory.getJobNodesRepository().updateJobNode(jobNodeId, jobNodeDetails);
        
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/add/input/{label}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER})
    public void addInputLabel(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId, 
        @PathVariable("label") @NotBlank @Size(max = 50) String label
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
    

        if( jobNode.getInput().containsKey(label)){
            throw new GeneralResponseException("Exception: input with label \"" + label +  "\" already exists");
        }  

        ChannelList channelList = repositoryFactory.getChannelListRepository().create();
        jobNode.getInput().put(label, channelList);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/add/output/{label}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER})
    public void addOutputChannel(
        @ProjectId @PathVariable("project_id") String projectId, 
        @JobNodeId @PathVariable("job_node_id") String jobNodeId, 
        @PathVariable("label") @NotBlank @Size(max = 50)  String label
    ){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
    

        if( jobNode.getOutput().containsKey(label)){
            throw new GeneralResponseException("Exception: output with label \"" + label +  "\" already exists");
        }  

        ChannelList channelList = repositoryFactory.getChannelListRepository().create();
        jobNode.getOutput().put(label, channelList);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    }


    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/remove/input/{label}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER})
    public void removeInputLabel(
        @ProjectId @PathVariable("project_id") String projectId, 
        @JobNodeId @PathVariable("job_node_id") String jobNodeId, 
        @PathVariable("label") @NotBlank @Size(max = 50)  String label
    ){
        
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
 
        if( !jobNode.getInput().containsKey(label)){
            throw new GeneralResponseException("Label " + label + " does not exist");
        }  

        for(Channel channel : jobNode.getInput().get(label).getChannelList()){
            channel.getOutputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));

            

            if(channel.getOutputJobs().size() == 0){
                
                // if the channel if part of project input, we skip it
                boolean isProjectInput = false;
                for(String key : project.getInputChannels().keySet()){
                    if(project.getInputChannels().get(key).getId().equals(channel.getId())){
                        isProjectInput = true;
                        break;
                    }
                }

                //otherwise delete it
                if(!isProjectInput){
                    channelController.deleteChannelById(projectId, channel.getId());
                }else{
                    repositoryFactory.getChannelsRepository().updateChannelFull(channel);
                }
                
            }else{
                repositoryFactory.getChannelsRepository().updateChannelFull(channel);
            }


        }

        repositoryFactory.getChannelListRepository().delete(jobNode.getInput().get(label).getId());
        jobNode.getInput().remove(label);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);

    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/remove/output/{label}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    @AuthorizeJobRoles(roles = {JobNodePrivilege.MANAGER})
    public void removeOutputLabel(
        @ProjectId @PathVariable("project_id") String projectId, 
        @JobNodeId @PathVariable("job_node_id") String jobNodeId, 
        @PathVariable("label") @NotBlank @Size(max = 50)  String label
    ){
        
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));
     
        if( !jobNode.getOutput().containsKey(label)){
            throw new GeneralResponseException("Label " + label + " does not exist");
        }  

        for(Channel channel : jobNode.getOutput().get(label).getChannelList()){
            channel.getInputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));

            

            if(channel.getInputJobs().size() == 0){
                
                // if the channel if part of project input, we skip it
                boolean isProjectInput = false;
                for(String key : project.getOutputChannels().keySet()){
                    if(project.getOutputChannels().get(key).getId().equals(channel.getId())){
                        isProjectInput = true;
                        break;
                    }
                }

                //otherwise delete it
                if(!isProjectInput){
                    channelController.deleteChannelById(projectId, channel.getId());
                }else{
                    repositoryFactory.getChannelsRepository().updateChannelFull(channel);
                }
                
            }else{
                repositoryFactory.getChannelsRepository().updateChannelFull(channel);
            }
        }

        repositoryFactory.getChannelListRepository().delete(jobNode.getOutput().get(label).getId());
        jobNode.getOutput().remove(label);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);


    }

 
    @PutMapping("/projects/{project_id}/job_nodes/connect")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public void connectJobNodes(
        @ProjectId @PathVariable(value="project_id") String projectId,
        @RequestParam(required = false, value="input_job_node_id") String inputJobNodeId,
        @RequestParam(required = false, value="output_job_node_id") String outputJobNodeId,
        @RequestParam(required = false, value="input_label") String inputJobNodeLabel,
        @RequestParam(required = false, value="output_label") String outputJobNodeLabel,
        @RequestParam(required = false, value ="project_input_label") String projectInputLabel,
        @RequestParam(required = false, value = "project_output_label") String projectOutputLabel,
        @RequestBody(required = false) @Valid ChannelDetails channelDetails

    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        //output job sends data, input job receives it
        JobNode inputJob = null, outputJob = null;
        if(inputJobNodeId != null){
            inputJob = repositoryFactory.getJobNodesRepository().retrieveById(inputJobNodeId)
                .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), inputJobNodeId));
        }
        if(outputJobNodeId != null){
            outputJob = repositoryFactory.getJobNodesRepository().retrieveById(outputJobNodeId)
                .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), outputJobNodeId));
        }

        // firstly, check if the parameters are alright

        // funciton works in one of three options or few of them, depending on parameters given
        if( !(
            outputJob != null && outputJobNodeLabel != null && projectOutputLabel != null
            ||
            inputJob != null && inputJobNodeLabel != null && projectInputLabel != null
            ||
            outputJob != null && inputJob != null && inputJobNodeLabel != null && outputJobNodeLabel != null
        )){
            throw new GeneralResponseException("Input and Output is not specified correctly");
        }

        // check first and second mods:
        if(outputJob != null && outputJobNodeLabel != null && projectOutputLabel != null){
            if(!project.getOutputChannels().containsKey(projectOutputLabel) || project.getOutputChannels().get(projectOutputLabel) == null){
                throw new GeneralResponseException("Output label does not exist");
            }
            if( !outputJob.getOutput().containsKey(outputJobNodeLabel) || outputJob.getOutput().get(outputJobNodeLabel) == null){
                throw new GeneralResponseException("Input label does not exist");
            }
        }

        if(inputJob != null && inputJobNodeLabel != null && projectInputLabel != null){
            if(!project.getInputChannels().containsKey(projectInputLabel) || project.getInputChannels().get(projectInputLabel) == null){
                throw new GeneralResponseException("Input label does not exist");
            }
            if( !inputJob.getInput().containsKey(inputJobNodeLabel) || inputJob.getInput().get(inputJobNodeLabel) == null){
                throw new GeneralResponseException("Output label does not exist");
            }
        }

        // check third mod:
        if(inputJob != null && inputJobNodeLabel != null && outputJob != null && outputJobNodeLabel != null){
            if(!inputJob.getInput().containsKey(inputJobNodeLabel) || inputJob.getInput().get(inputJobNodeLabel) == null){
                throw new GeneralResponseException("Output label does not exist");
            }
            
            if(!outputJob.getOutput().containsKey(outputJobNodeLabel) || outputJob.getOutput().get(outputJobNodeLabel) == null){
                throw new GeneralResponseException("Input label does not exist");
            }
        }


        //connect output job node to output project label
        if(projectOutputLabel != null && outputJob != null && outputJobNodeLabel != null){

            Channel channel = project.getOutputChannels().get(projectOutputLabel);

            outputJob.getOutput().get(outputJobNodeLabel).getChannelList().add(channel);
            repositoryFactory.getChannelListRepository().update(outputJob.getOutput().get(outputJobNodeLabel));
            channel.getInputJobs().add(outputJob);

            repositoryFactory.getChannelsRepository().updateChannelFull(channel);
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(outputJob);
        }

        //connect input job node to input project label
        if(projectInputLabel != null && inputJob != null && inputJobNodeLabel != null){
            Channel channel = project.getInputChannels().get(projectInputLabel);

            inputJob.getInput().get(inputJobNodeLabel).getChannelList().add(channel);
            repositoryFactory.getChannelListRepository().update(inputJob.getInput().get(inputJobNodeLabel));

            channel.getOutputJobs().add(inputJob);

            repositoryFactory.getChannelsRepository().updateChannelFull(channel);
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(inputJob);
        }

        //connect input and output jobs
        if(inputJob != null && inputJobNodeLabel != null &&  outputJob != null && outputJobNodeLabel != null){

            Channel channel = repositoryFactory.getChannelsRepository().createChannel(project, channelDetails);

            inputJob.getInput().get(inputJobNodeLabel).getChannelList().add(channel); 
            outputJob.getOutput().get(outputJobNodeLabel).getChannelList().add(channel);  
            repositoryFactory.getChannelListRepository().update(inputJob.getInput().get(inputJobNodeLabel));
            repositoryFactory.getChannelListRepository().update(outputJob.getOutput().get(outputJobNodeLabel));


            channel.getInputJobs().add(outputJob);
            channel.getOutputJobs().add(inputJob);

            repositoryFactory.getChannelsRepository().updateChannelFull(channel);
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(inputJob);
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(outputJob);

            project.getChannels().add(channel);
            repositoryFactory.getProjectRepository().updateProjectFull(project);
        }
    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public void deleteJobNode(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId
    ){

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
            .orElseThrow(() -> new ResourceNotFoundException(JobNode.class.getSimpleName(), jobNodeId));


        if(jobNode.getIlumGroup() != null){
            throw new GeneralResponseException("The job node is running right now and cannot be deleted!");
        }

        // // remove the channels, that where connecting to project input/output, but dont delete them
        // for(String label : jobNode.getInput().keySet()){
        //     for(Channel channel : jobNode.getInput().get(label).getChannelList()){
        //         for(String labelProject : project.getInputChannels().keySet()){
        //             if(project.getInputChannels().get(labelProject).getId().equals(channel.getId())){
        //                 jobNode.getInput().get(label).getChannelList().remove(channel);
        //                 repositoryFactory.getChannelListRepository().update(jobNode.getInput().get(label));

        //                 channel.getOutputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
        //             }
        //         }
        //     }
        // }

        // for(String label : jobNode.getOutput().keySet()){
        //     for(Channel channel : jobNode.getInput().get(label).getChannelList()){
        //         for(String labelProject : project.getOutputChannels().keySet()){
        //             if(project.getOutputChannels().get(labelProject).getId().equals(channel.getId())){
        //                 jobNode.getOutput().get(label).getChannelList().remove(channel);
        //                 repositoryFactory.getChannelListRepository().update(jobNode.getOutput().get(label));

        //                 channel.getInputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
        //             }
        //         }
        //     }
        // }
        // project.getJobNodes().removeIf(jn -> jn.getId().equals(jobNodeId));
        // repositoryFactory.getProjectRepository().updateProjectFull(project);

        // // remove the channels that were connecting this jobnode with other, while also deleting it if possible
        // for(String label : jobNode.getInput().keySet()){
        //     for(Channel channel : jobNode.getInput().get(label).getChannelList()){
        //         channel.getOutputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
        //         if(channel.getOutputJobs().size() == 0){
        //             channelController.deleteChannelById(projectId, channel.getId());
        //         }
        //     }
        // }
        // for(String label : jobNode.getOutput().keySet()){
        //     for(Channel channel : jobNode.getOutput().get(label).getChannelList()){
        //         channel.getInputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
        //         if(channel.getInputJobs().size() == 0){
        //             channelController.deleteChannelById(projectId, channel.getId());
        //         }
        //     }
        // }
        repositoryFactory.getJobNodesRepository().deleteJobNodeById(jobNodeId);
    }


}
