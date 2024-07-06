package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobsFile {
    
    @Id
    private String id;
    private String extension;
    private JobsFileDetails jobDetails;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_project_reference")
    private Project project;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_job_node_reference")
    private JobNode jobNode;

    @DBRef(lazy = true)
    private AppUser publisher;
    
    private Set<String> allClasses = new HashSet<>();


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JobsFile other = (JobsFile) obj;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName() + "_" + id);
    }

}
