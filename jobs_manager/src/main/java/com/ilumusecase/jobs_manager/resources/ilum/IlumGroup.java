package com.ilumusecase.jobs_manager.resources.ilum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class IlumGroup {
    
    @Id
    private String id;
    private String ilumId;

    private IlumGroupConfiguraion ilumGroupConfiguraion;

    private int currentIndex = 0;
    private int currentTestingIndex = 0;
    private String mod = "NORMAL";

    @DBRef(lazy = true)
    @JsonFilter("ilum_group_jobs")
    private JobEntity currentJob;
    private LocalDateTime currentStartTime;

    @DBRef(lazy = true)    
    @JsonFilter("ilum_group_jobs")
    private List<JobEntity> jobs = new ArrayList<>();

    @DBRef(lazy = true)
    @JsonFilter("ilum_group_jobs")
    private List<JobEntity> testingJobs = new ArrayList<>();


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
        IlumGroup other = (IlumGroup) obj;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName() + "_" + id);
    }
}
