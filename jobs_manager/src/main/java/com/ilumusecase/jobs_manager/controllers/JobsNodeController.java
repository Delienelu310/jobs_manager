package com.ilumusecase.jobs_manager.controllers;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.Channel;
import com.ilumusecase.jobs_manager.resources.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.JobNode;
import com.ilumusecase.jobs_manager.resources.JobNodeDetails;
import com.ilumusecase.jobs_manager.resources.Project;


@RestController
public class JobsNodeController {

    @Autowired
    private RepositoryFactory repositoryFactory;
    @Autowired
    private JsonMappersFactory jsonMappersFactory;

    @Autowired
    private ChannelController channelController;

    @GetMapping("/job_nodes")
    public MappingJacksonValue retrieveAllNodes(){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNodeList(
            repositoryFactory.getJobNodesRepository().retrieveAll()
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}}")
    public MappingJacksonValue retrieveById(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode( 
            repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId)
        );
    }

    @GetMapping("/projects/{project_id}/job_nodes")
    public MappingJacksonValue retrieveJobNodesByProjectId(@PathVariable("project_id") String projectId){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNodeList(
            repositoryFactory.getJobNodesRepository().retrieveByProjectId(projectId)
        );
    }

    @PostMapping("/projects/{project_id}/job_nodes")
    public MappingJacksonValue createJobNode(@PathVariable("project_id") String projectId, @RequestBody JobNodeDetails jobNodeDetails){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode(
            repositoryFactory.getJobNodesRepository().createJobNode(
                repositoryFactory.getProjectRepository().retrieveProjectById(projectId), 
                jobNodeDetails
            )
        );
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}")
    public MappingJacksonValue updateJobNodeDetails(@PathVariable("job_node_id") String jobNodeId, @RequestBody JobNodeDetails jobNodeDetails){
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode(
            repositoryFactory.getJobNodesRepository().updateJobNode(jobNodeId, jobNodeDetails)
        );
    }

    @PutMapping("/projects/{project_id}/job_nodes/connect")
    public MappingJacksonValue connectJobNodes(
        @PathVariable("project_id") String projectId,
        @RequestParam("input_job_node_id") String inputJobNodeId,
        @RequestParam("output_job_node_id") String outputJobNodeId,
        @RequestParam("input_label") String inputJobNodeLabel,
        @RequestParam("output_label") String outputJobNodeLabel,
        @RequestParam("project_input_label") String projectInputLabel,
        @RequestParam("project_output_label") String projectOutputLabel,
        @RequestBody ChannelDetails channelDetails

    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(outputJobNodeId);
        //output job send data, input job receives it
        JobNode inputJob = null, outputJob = null;
        if(inputJobNodeId != null){
            inputJob = repositoryFactory.getJobNodesRepository().retrieveById(inputJobNodeId);
        }
        if(outputJobNodeId != null){
            outputJob = repositoryFactory.getJobNodesRepository().retrieveById(outputJobNodeId);
        }

        // firstly, check if the parameters are alright
        if(projectOutputLabel != null && outputJob != null){
            Channel channel = project.getOutputChannels().get(projectOutputLabel);
            if(outputJobNodeLabel != null && 
                outputJob.getOutput().get(outputJobNodeLabel).stream().anyMatch(ch -> ch.getId().equals(channel.getId()))
            ){
                throw new RuntimeException();
            }
        }

        if(projectInputLabel != null && inputJob != null){
            Channel channel = project.getInputChannels().get(projectInputLabel);
            if(inputJobNodeLabel != null && 
                inputJob.getInput().get(inputJobNodeLabel).stream().anyMatch(ch -> ch.getId().equals(channel.getId()))
            ){
                throw new RuntimeException();
            }
        }

        //connect output job node to output project label
        if(projectOutputLabel != null && outputJob != null){


            //if it is no connected yet, 

            Channel channel = project.getOutputChannels().get(projectOutputLabel);
            if(outputJobNodeLabel != null){
                outputJob.getOutput().get(outputJobNodeLabel).add(channel);
            }else{
                List<Channel> newOutputJobLabel = new ArrayList<>();
                newOutputJobLabel.add(channel);
                outputJob.getOutput().put(channel.getId() + "_" + projectOutputLabel, newOutputJobLabel);
            }
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(outputJob);
        }

        //connect input job node to input project label
        if(projectInputLabel != null && inputJob != null){
            Channel channel = project.getInputChannels().get(projectInputLabel);
            if(inputJobNodeLabel != null){
                inputJob.getInput().get(inputJobNodeLabel).add(channel);
            }else{
                List<Channel> newInputJobLabel = new ArrayList<>();
                newInputJobLabel.add(channel);
                outputJob.getOutput().put(channel.getId() + "_" + projectInputLabel, newInputJobLabel);
            }
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(inputJob);
        }

        //connect input and output jobs
        if(inputJob != null && outputJob != null){
            Channel channel = repositoryFactory.getChannelsRepository().createChannel(project, channelDetails);

        
            if(inputJobNodeLabel != null){
                if(inputJob.getInput().containsKey(inputJobNodeLabel)){
                    inputJob.getInput().put(inputJobNodeLabel, new ArrayList<>());
                }
                inputJob.getInput().get(inputJobNodeLabel).add(channel);        
            }else{
                List<Channel> labelList = new ArrayList<>();
                labelList.add(channel);
                inputJob.getInput().put(channel.getId(), labelList);
            }

            if(outputJobNodeLabel != null){
                if(outputJob.getOutput().containsKey(outputJobNodeLabel)){
                    outputJob.getOutput().put(outputJobNodeLabel, new ArrayList<>());
                }
                outputJob.getOutput().get(outputJobNodeLabel).add(channel);        
            }else{
                List<Channel> labelList = new ArrayList<>();
                labelList.add(channel);
                outputJob.getOutput().put(channel.getId(), labelList);
            }

            repositoryFactory.getJobNodesRepository().updateJobNodeFull(inputJob);
            repositoryFactory.getJobNodesRepository().updateJobNodeFull(outputJob);
        }


        return null;
    }

    @DeleteMapping("/projects/{project_id}/job_nodes/{job_node_id}")
    public void deleteJobNode(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId){

        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !jobNode.getProject().getId().equals(projectId) ){
            throw new RuntimeException();
        }


        // remove the channels, that where connecting to project input/output, but dont delete them
        for(String label : jobNode.getInput().keySet()){
            for(Channel channel : jobNode.getInput().get(label)){
                for(String labelProject : project.getInputChannels().keySet()){
                    if(project.getInputChannels().get(labelProject).getId().equals(channel.getId())){
                        jobNode.getInput().get(label).remove(channel);
                        channel.getOutputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
                    }
                }
            }
        }

        for(String label : jobNode.getOutput().keySet()){
            for(Channel channel : jobNode.getInput().get(label)){
                for(String labelProject : project.getOutputChannels().keySet()){
                    if(project.getOutputChannels().get(labelProject).getId().equals(channel.getId())){
                        jobNode.getOutput().get(label).remove(channel);
                        channel.getInputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
                    }
                }
            }
        }
        project.getJobNodes().removeIf(jn -> jn.getId().equals(jobNodeId));
        repositoryFactory.getProjectRepository().updateProjectFull(project);

        // remove the channels that were connecting this jobnode with other, while also deleting it if possible
        for(String label : jobNode.getInput().keySet()){
            for(Channel channel : jobNode.getInput().get(label)){
                channel.getOutputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
                if(channel.getOutputJobs().size() == 0){
                    channelController.deleteChannelById(projectId, channel.getId());
                }
            }
            jobNode.getInput().remove(label);
        }
        for(String label : jobNode.getOutput().keySet()){
            for(Channel channel : jobNode.getOutput().get(label)){
                channel.getInputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
                if(channel.getInputJobs().size() == 0){
                    channelController.deleteChannelById(projectId, channel.getId());
                }
            }
        }
        repositoryFactory.getJobNodesRepository().deleteJobNodeById(jobNodeId);
    }


   
}
