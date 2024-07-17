package com.ilumusecase.jobs_manager.controllers.ui_controllers;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.ui.JobNodeVertice;
import com.ilumusecase.jobs_manager.resources.ui.ProjectGraph;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

@RestController
public class ProjectGraphController {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @GetMapping("/projects/{project_id}/graph")
    @JsonMapperRequest(type="graph", resource = "ProjectGraph")
    public Object retrieveProjectGraph(
        @ProjectId @PathVariable("project_id") String projectId
    ){
        return repositoryFactory.getProjectGraphRepository().retrieveProjectGraphByProjectId(projectId).orElseThrow();
    }



    //refresh the project graph, so that it would include new jobnodes, or create it, if it does not exist
    @PutMapping("/projects/{project_id}/graph")
    @JsonMapperRequest(type="graph", resource = "ProjectGraph")
    public Object updateProjectGraph(
        @ProjectId @PathVariable("project_id") String projectId
    ){
        Project project = repositoryFactory.getProjectRepository().retrieveProjectById(projectId);
        Optional<ProjectGraph> projectGraphOptional = repositoryFactory.getProjectGraphRepository().retrieveProjectGraphByProjectId(projectId);
        if(projectGraphOptional.isEmpty()){
            ProjectGraph newProjectGraph = new ProjectGraph();
            newProjectGraph.setProject(project);

            projectGraphOptional = Optional.of(repositoryFactory.getProjectGraphRepository().updateProjectGraph(newProjectGraph));
        }

        ProjectGraph projectGraph = projectGraphOptional.get();

        Set<String> presentJobNodesIds = projectGraph.getVertices().stream().map(v -> v.getJobNode().getId()).collect(Collectors.toSet());
        Set<String> actualJobNodesIds = project.getJobNodes().stream().map(j -> j.getId()).collect(Collectors.toSet());
        for(JobNode jobNode : project.getJobNodes()){
            if(presentJobNodesIds.contains(jobNode.getId())) continue;
            else{

                JobNodeVertice jobNodeVertice = new JobNodeVertice();
                jobNodeVertice.setX(0);
                jobNodeVertice.setY(0);
                jobNodeVertice.setJobNode(jobNode);

                projectGraph.getVertices().add(
                    repositoryFactory.getJobNodeVerticeRepository().updateJobNodeVertice(jobNodeVertice)
                );
            }
        }
        for(JobNodeVertice vertice : projectGraph.getVertices()){
            if(!actualJobNodesIds.contains(vertice.getJobNode().getId())){
                projectGraph.getVertices().remove(vertice);
            }
        }
        return repositoryFactory.getProjectGraphRepository().updateProjectGraph(projectGraph);

    }


    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/graph")
    @JsonMapperRequest(type="graph", resource = "JobNodeVertice")
    public Object updateJobNodeVertice(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestBody JobNodeVertice jobNodeVertice
    ){

        JobNodeVertice jobNodeVerticeActual = repositoryFactory.getJobNodeVerticeRepository().retrieveByJobNodeId(jobNodeId).orElseThrow();

        jobNodeVerticeActual.setX(jobNodeVertice.getX());
        jobNodeVerticeActual.setY(jobNodeVertice.getY());

        return repositoryFactory.getJobNodeVerticeRepository().updateJobNodeVertice(jobNodeVerticeActual);
    }
}
