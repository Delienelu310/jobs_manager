package com.ilumusecase.jobs_manager.resources.abstraction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.JobEntity;
import com.ilumusecase.jobs_manager.resources.ilum.JobScript;
import com.ilumusecase.jobs_manager.resources.ilum.JobsFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobNode {

    @Id
    private String id;

    @NotNull
    @Valid
    private JobNodeDetails jobNodeDetails;


    @DBRef
    @JsonFilter("project-reference")
    @NotNull
    @Valid
    private Project project;
    
    @DBRef(lazy = true)
    @NotNull
    @Valid
    private Map<String, ChannelList> input = new HashMap<>();
    
    @DBRef(lazy = true)
    @NotNull
    @Valid
    private Map<String, ChannelList> output = new HashMap<>();


    @DBRef(lazy = true)
    @NotNull
    @Valid
    private Map<String, PrivilegeList<JobNodePrivilege>> privileges = new HashMap<>();


    @DBRef(lazy = true)
    @JsonFilter("job_node_jobs_files")
    @NotNull
    @Valid
    private List<JobsFile> jobsFiles = new LinkedList<>();
    
    @DBRef(lazy = true)
    @JsonFilter("job_node_job_scripts")
    @NotNull
    @Valid
    private List<JobScript> jobScripts = new LinkedList<>();

    @DBRef(lazy = true)
    @JsonFilter("job_node_job_results")
    @NotNull
    @Valid
    private List<JobEntity> jobResults = new LinkedList<>();




    @DBRef(lazy = true)
    @JsonFilter("job_node_jobs_queue")
    @NotNull
    @Valid
    private List<JobEntity> testingJobs = new LinkedList<>();

    @DBRef(lazy = true)
    @JsonFilter("job_node_jobs_queue")
    @NotNull
    @Valid
    private List<JobEntity> jobsQueue = new LinkedList<>();

    @DBRef(lazy = true)
    @JsonFilter("job_node_ilum_groups")
    @Valid
    private IlumGroup ilumGroup = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JobNode other = (JobNode) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }




}
