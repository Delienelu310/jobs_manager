package com.ilumusecase.jobs_manager.controllers.ui_controllers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ilumusecase.jobs_manager.exceptions.ResourceNotFoundException;
import com.ilumusecase.jobs_manager.json_mappers.JsonMapperRequest;
import com.ilumusecase.jobs_manager.repositories.interfaces.RepositoryFactory;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.resources.ui.JobNodeVertice;
import com.ilumusecase.jobs_manager.resources.ui.ProjectGraph;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.AuthorizeProjectRoles;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.JobNodeId;
import com.ilumusecase.jobs_manager.security.authorizationAspectAnnotations.ProjectId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@Validated
public class ProjectGraphController {

    @Autowired
    private RepositoryFactory repositoryFactory;

    @GetMapping("/projects/{project_id}/graph")
    @JsonMapperRequest(type="graph", resource = "ProjectGraph")
    @AuthorizeProjectRoles
    public Object retrieveProjectGraph(
        @ProjectId @PathVariable("project_id") String projectId
    ){
        return repositoryFactory.getProjectGraphRepository().retrieveProjectGraphByProjectId(projectId).orElseThrow(() -> new ResourceNotFoundException(ProjectGraph.class.getSimpleName(), projectId));
    }



    //refresh the project graph, so that it would include new jobnodes, or create it, if it does not exist
    @PutMapping("/projects/{project_id}/graph")
    @JsonMapperRequest(type="graph", resource = "ProjectGraph")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
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
        Set<JobNodeVertice> toRemove =  new HashSet<>();
        for(JobNodeVertice vertice : projectGraph.getVertices()){
            if(!actualJobNodesIds.contains(vertice.getJobNode().getId())){
                toRemove.add(vertice);
            }
        }
        for(JobNodeVertice vertice : toRemove){
            projectGraph.getVertices().remove(vertice);
        }

        return repositoryFactory.getProjectGraphRepository().updateProjectGraph(projectGraph);

    }

    private record JobNodeVerticeCoords(@NotNull Integer x, @NotNull Integer y){}


    @PutMapping("/projects/{project_id}/job_nodes/{job_node_id}/graph")
    @AuthorizeProjectRoles(roles = {ProjectPrivilege.ADMIN, ProjectPrivilege.MODERATOR, ProjectPrivilege.ARCHITECT})
    public void updateJobNodeVertice(
        @ProjectId @PathVariable("project_id") String projectId,
        @JobNodeId @PathVariable("job_node_id") String jobNodeId,
        @RequestBody @Valid @NotNull  JobNodeVerticeCoords jobNodeVertice
    ){

        JobNodeVertice jobNodeVerticeActual = repositoryFactory.getJobNodeVerticeRepository().retrieveByJobNodeId(jobNodeId).orElseThrow(
            () -> new ResourceNotFoundException(JobNodeVertice.class.getSimpleName(), jobNodeId)
        );

        jobNodeVerticeActual.setX(jobNodeVertice.x());
        jobNodeVerticeActual.setY(jobNodeVertice.y());

        repositoryFactory.getJobNodeVerticeRepository().updateJobNodeVertice(jobNodeVerticeActual);
    }
}
