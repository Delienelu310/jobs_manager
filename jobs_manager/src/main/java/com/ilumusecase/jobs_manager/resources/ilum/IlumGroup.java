package com.ilumusecase.jobs_manager.resources.ilum;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class IlumGroup {
    
    @Id
    private String id;
    private String ilumId;

    @Valid
    @NotNull
    private IlumGroupConfiguraion ilumGroupConfiguraion;
    @Valid
    @NotNull
    private IlumGroupDetails ilumGroupDetails;


    private int currentIndex = 0;
    private int currentTestingIndex = 0;
    private String mod = "NORMAL";

    @DBRef(lazy = true)
    @JsonFilter("ilum_group_jobs")
    @Valid
    private JobEntity currentJob;
    private LocalDateTime currentStartTime;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_project_reference")
    @NotNull
    private Project project;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_job_node_reference")
    @NotNull
    private JobNode jobNode;

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IlumGroup other = (IlumGroup) obj;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName() + "_" + id);
    }
}
