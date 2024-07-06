package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobEntity {

    @Id
    private String id;

    // @DBRef(lazy = true)
    // @JsonFilter("job_entity_ilum_group")
    // private IlumGroup ilumGroup;

    private String ilumId;

    @DBRef(lazy = true)
    @JsonFilter("job_entity_job_script")
    private JobScript jobScript;
    private String configuration;
    private JobEntityDetails jobEntityDetails;

    private String state;



    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_job_node_reference")
    private JobNode jobNode;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_project_reference")
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
