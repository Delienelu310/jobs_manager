package com.ilumusecase.jobs_manager.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class Project {
    
    @Id
    private String id;

    private ProjectDetails projectDetails;

    @DBRef(lazy=true)
    @JsonFilter("plug-channel")
    private List<Channel> channels = new ArrayList<>();
    @DBRef(lazy=true)
    @JsonFilter("plug-jobNode")
    private List<JobNode> jobNodes = new ArrayList<>(); 

    @DBRef(lazy=true)
    @JsonFilter("plug-channel")
    private Map<String, Channel> inputChannels = new HashMap<>();
    @DBRef(lazy=true)
    @JsonFilter("plug-channel")
    private Map<String, Channel> outputChannels = new HashMap<>();

    @DBRef(lazy = true)
    private Map<AppUser, PrivilegeList<ProjectPrivilege>> privileges = new HashMap<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Project other = (Project) obj;
        return id == other.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

