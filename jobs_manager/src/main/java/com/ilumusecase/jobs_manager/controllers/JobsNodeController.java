package com.ilumusecase.jobs_manager.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.ilumusecase.jobs_manager.JobsManagerApplication;
import com.ilumusecase.jobs_manager.json_mappers.JsonMappersFactory;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.Channel;
import com.ilumusecase.jobs_manager.resources.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.ChannelList;
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

    @GetMapping("/projects/{project_id}/job_nodes/{job_node_id}")
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
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().createJobNode( project, jobNodeDetails);

        
        project.getJobNodes().add(jobNode);
        repositoryFactory.getProjectRepository().updateProjectFull(project);

        
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode(jobNode);
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}")
    public MappingJacksonValue updateJobNodeDetails(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId, @RequestBody JobNodeDetails jobNodeDetails){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        if(!project.getJobNodes().stream().anyMatch(jn -> jn.getId().equals(jobNodeId))){
            throw new RuntimeException();
        }
        
        return jsonMappersFactory.getJobNodeJsonMapper().getFullJobNode(
            repositoryFactory.getJobNodesRepository().updateJobNode(jobNodeId, jobNodeDetails)
        );
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/add/input/{label}")
    public void addInputLabel(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId, @PathVariable("label") String label){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !jobNode.getProject().getId().equals(projectId)){
            throw new RuntimeException();
        }

        if( jobNode.getInput().containsKey(label)){
            throw new RuntimeException();
        }  

        ChannelList channelList = repositoryFactory.getChannelListRepository().create();
        jobNode.getInput().put(label, channelList);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    }

    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/add/output/{label}")
    public void addOutputChannel(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId, @PathVariable("label") String label){
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !jobNode.getProject().getId().equals(projectId)){
            throw new RuntimeException();
        }

        if( jobNode.getOutput().containsKey(label)){
            throw new RuntimeException();
        }  

        ChannelList channelList = repositoryFactory.getChannelListRepository().create();
        jobNode.getOutput().put(label, channelList);
        repositoryFactory.getJobNodesRepository().updateJobNodeFull(jobNode);
    }


    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/remove/input/{label}")
    public void removeInputLabel(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId, @PathVariable("label") String label){
        
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !jobNode.getProject().getId().equals(projectId)){
            throw new RuntimeException();
        }

        if( !jobNode.getInput().containsKey(label)){
            throw new RuntimeException();
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
    public void removeOutputLabel(@PathVariable("project_id") String projectId, @PathVariable("job_node_id") String jobNodeId, @PathVariable("label") String label){
        
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        JobNode jobNode = repositoryFactory.getJobNodesRepository().retrieveById(jobNodeId);
        if( !jobNode.getProject().getId().equals(projectId)){
            throw new RuntimeException();
        }

        if( !jobNode.getOutput().containsKey(label)){
            throw new RuntimeException();
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

    Logger logger = LoggerFactory.getLogger(JobsManagerApplication.class);


    @PutMapping("/projects/{project_id}/job_nodes/connect")
    public void connectJobNodes(
        @PathVariable(value="project_id") String projectId,
        @RequestParam(required = false, value="input_job_node_id") String inputJobNodeId,
        @RequestParam(required = false, value="output_job_node_id") String outputJobNodeId,
        @RequestParam(required = false, value="input_label") String inputJobNodeLabel,
        @RequestParam(required = false, value="output_label") String outputJobNodeLabel,
        @RequestParam(required = false, value ="project_input_label") String projectInputLabel,
        @RequestParam(required = false, value = "project_output_label") String projectOutputLabel,
        @RequestBody(required = false) ChannelDetails channelDetails

    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        //output job sends data, input job receives it
        JobNode inputJob = null, outputJob = null;
        if(inputJobNodeId != null){
            inputJob = repositoryFactory.getJobNodesRepository().retrieveById(inputJobNodeId);
        }
        if(outputJobNodeId != null){
            outputJob = repositoryFactory.getJobNodesRepository().retrieveById(outputJobNodeId);
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
            throw new RuntimeException();
        }

        // check first and second mods:
        if(outputJob != null && outputJobNodeLabel != null && projectOutputLabel != null){
            if(!project.getOutputChannels().containsKey(projectOutputLabel) || project.getOutputChannels().get(projectOutputLabel) == null){
                throw new RuntimeException();
            }
            if( !outputJob.getOutput().containsKey(outputJobNodeLabel) || outputJob.getOutput().get(outputJobNodeLabel) == null){
                throw new RuntimeException();
            }
        }

        if(inputJob != null && inputJobNodeLabel != null && projectInputLabel != null){
            if(!project.getInputChannels().containsKey(projectInputLabel) || project.getInputChannels().get(projectInputLabel) == null){
                throw new RuntimeException();
            }
            if( !inputJob.getInput().containsKey(inputJobNodeLabel) || inputJob.getInput().get(inputJobNodeLabel) == null){
                throw new RuntimeException();
            }
        }

        // check third mod:
        if(inputJob != null && inputJobNodeLabel != null && outputJob != null && outputJobNodeLabel != null){
            if(!inputJob.getInput().containsKey(inputJobNodeLabel) || inputJob.getInput().get(inputJobNodeLabel) == null){
                throw new RuntimeException();
            }
            
            if(!outputJob.getOutput().containsKey(outputJobNodeLabel) || outputJob.getOutput().get(outputJobNodeLabel) == null){
                throw new RuntimeException();
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
        }
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
            for(Channel channel : jobNode.getInput().get(label).getChannelList()){
                for(String labelProject : project.getInputChannels().keySet()){
                    if(project.getInputChannels().get(labelProject).getId().equals(channel.getId())){
                        jobNode.getInput().get(label).getChannelList().remove(channel);
                        repositoryFactory.getChannelListRepository().update(jobNode.getInput().get(label));

                        channel.getOutputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
                    }
                }
            }
        }

        for(String label : jobNode.getOutput().keySet()){
            for(Channel channel : jobNode.getInput().get(label).getChannelList()){
                for(String labelProject : project.getOutputChannels().keySet()){
                    if(project.getOutputChannels().get(labelProject).getId().equals(channel.getId())){
                        jobNode.getOutput().get(label).getChannelList().remove(channel);
                        repositoryFactory.getChannelListRepository().update(jobNode.getOutput().get(label));

                        channel.getInputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
                    }
                }
            }
        }
        project.getJobNodes().removeIf(jn -> jn.getId().equals(jobNodeId));
        repositoryFactory.getProjectRepository().updateProjectFull(project);

        // remove the channels that were connecting this jobnode with other, while also deleting it if possible
        for(String label : jobNode.getInput().keySet()){
            for(Channel channel : jobNode.getInput().get(label).getChannelList()){
                channel.getOutputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
                if(channel.getOutputJobs().size() == 0){
                    channelController.deleteChannelById(projectId, channel.getId());
                }
            }
            jobNode.getInput().remove(label);
        }
        for(String label : jobNode.getOutput().keySet()){
            for(Channel channel : jobNode.getOutput().get(label).getChannelList()){
                channel.getInputJobs().removeIf(jn -> jn.getId().equals(jobNodeId));
                if(channel.getInputJobs().size() == 0){
                    channelController.deleteChannelById(projectId, channel.getId());
                }
            }
        }
        repositoryFactory.getJobNodesRepository().deleteJobNodeById(jobNodeId);
    }


   
}
