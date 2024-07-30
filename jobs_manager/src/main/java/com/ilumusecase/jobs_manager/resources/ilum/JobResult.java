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
public class JobResult {

    @Id
    private String id;
    private String ilumId;

    private String ilumGroupId;

    private String targetConfiguration;
    @DBRef(lazy = true)
    @JsonFilter("job_result_job_script")
    private JobScript tester;
    @DBRef(lazy = true)
    @JsonFilter("job_result_job_script")
    private JobScript target;


    private Long startTime;
    private Long endTime;
    private JobResultDetails jobResultDetails; 



    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_project_reference")
    private Project project;
    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_job_node_reference")
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
