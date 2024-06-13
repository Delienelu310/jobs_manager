package com.ilumusecase.jobs_manager.resources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;

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
    private IlumGroup currentGroup;

    @DBRef(lazy = true)
    private List<JobsFile> jobsFiles = new LinkedList<>();
    
    @DBRef(lazy = true)
    private List<JobEntity> testingJobs = new LinkedList<>();

    @DBRef(lazy = true)
    private List<JobEntity> jobsQueue = new LinkedList<>();


    private Map<String, Integer> usedClassnames = new HashMap<>();
    private Set<String> jobClasses = new HashSet<>();

}
