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

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobNode {

    @Id
    private String id;
    
    private JobNodeDetails jobNodeDetails;


    @DBRef
    @JsonFilter("project-reference")
    private Project project;
    
    @DBRef(lazy = true)
    private Map<String, ChannelList> input = new HashMap<>();
    
    @DBRef(lazy = true)
    private Map<String, ChannelList> output = new HashMap<>();


    @DBRef(lazy = true)
    private Map<String, PrivilegeList<JobNodePrivilege>> privileges = new HashMap<>();


    @DBRef(lazy = true)
    @JsonFilter("job_node_jobs_files")
    private List<JobsFile> jobsFiles = new LinkedList<>();
    
    @DBRef(lazy = true)
    @JsonFilter("job_node_job_scripts")
    private List<JobScript> jobScripts = new LinkedList<>();

    @DBRef(lazy = true)
    @JsonFilter("job_node_job_entities")
    private List<JobEntity> jobEntities = new LinkedList<>();

    @DBRef(lazy = true)
    @JsonFilter("job_node_job_results")
    private List<JobEntity> jobResults = new LinkedList<>();




    @DBRef(lazy = true)
    @JsonFilter("job_node_jobs_queue")
    private List<JobEntity> testingJobs = new LinkedList<>();

    @DBRef(lazy = true)
    @JsonFilter("job_node_jobs_queue")
    private List<JobEntity> jobsQueue = new LinkedList<>();

    @DBRef(lazy = true)
    @JsonFilter("job_node_ilum_groups")
    private List<IlumGroup> ilumGroups = new LinkedList<>();

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
