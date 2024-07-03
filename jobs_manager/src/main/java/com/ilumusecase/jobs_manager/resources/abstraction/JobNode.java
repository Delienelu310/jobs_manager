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
    private List<JobsFile> jobsFiles = new LinkedList<>();
    
    @DBRef(lazy = true)
    private List<JobScript> jobScripts = new LinkedList<>();



    @DBRef(lazy = true)
    private List<JobEntity> testingJobs = new LinkedList<>();

    @DBRef(lazy = true)
    private List<JobEntity> jobsQueue = new LinkedList<>();

    @DBRef(lazy = true)
    private IlumGroup currentGroup;

}