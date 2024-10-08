package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;
import com.ilumusecase.jobs_manager.validation.annotations.JsonString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobEntity {

    @Id
    private String id;
    
    private String ilumId;

    @DBRef(lazy = true)
    @JsonFilter("job_entity_job_script")
    @NotNull
    private JobScript jobScript;

    @JsonString
    @NotNull
    private String configuration;
    
    @NotNull
    @Valid
    private JobEntityDetails jobEntityDetails;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_publisher")
    @NotNull
    private AppUser author; 

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_job_node_reference")
    @NotNull
    private JobNode jobNode;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_project_reference")
    @NotNull
    private Project project;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JobEntity other = (JobEntity) obj;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName() + "_" + id);
    }
}
