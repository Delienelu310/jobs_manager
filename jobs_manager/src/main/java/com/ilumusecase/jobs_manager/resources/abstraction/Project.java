package com.ilumusecase.jobs_manager.resources.abstraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;
import com.ilumusecase.jobs_manager.resources.authorities.ProjectPrivilege;
import com.ilumusecase.jobs_manager.validation.annotations.Username;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@JsonFilter("project")
public class Project {
    
    @Id
    private String id;

    @NotNull
    @Valid
    private ProjectDetails projectDetails;

    @Username
    private String admin;

    @DBRef(lazy=true)
    @JsonFilter("plug-channel")
    @NotNull
    @Valid
    private List<Channel> channels = new ArrayList<>();

    @DBRef(lazy=true)
    @JsonFilter("plug-jobNode")
    @NotNull
    @Valid
    private List<JobNode> jobNodes = new ArrayList<>(); 

    @DBRef(lazy=true)
    @JsonFilter("plug-channel")
    @NotNull
    @Valid
    private Map<String, Channel> inputChannels = new HashMap<>();

    @DBRef(lazy=true)
    @JsonFilter("plug-channel")
    @NotNull
    @Valid
    private Map<String, Channel> outputChannels = new HashMap<>();

    @DBRef(lazy = true)
    @NotNull
    @Valid
    private Map<String, PrivilegeList<ProjectPrivilege>> privileges = new HashMap<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Project other = (Project) obj;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

