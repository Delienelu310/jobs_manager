package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.validation.annotations.JsonString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobResult {

    @Id
    private String id;

    @NotNull
    private String ilumId;
    @NotNull
    private String ilumGroupId;
    @NotNull
    @Valid
    private IlumGroupDetails ilumGroupDetails;

    @JsonString
    private String targetConfiguration;

    @DBRef(lazy = true)
    @JsonFilter("job_result_job_script")
    private JobScript tester;
    @DBRef(lazy = true)
    @JsonFilter("job_result_job_script")
    private JobScript target;

    @Min(0)
    private Long startTime;
    @Min(0)
    private Long endTime;

    @Valid
    @NotNull
    private JobResultDetails jobResultDetails; 



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
        JobResult other = (JobResult) obj;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName() + "_" + id);
    }

}
