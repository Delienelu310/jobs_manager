package com.ilumusecase.jobs_manager.resources.ilum;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.ilumusecase.jobs_manager.resources.abstraction.JobNode;
import com.ilumusecase.jobs_manager.resources.abstraction.Project;
import com.ilumusecase.jobs_manager.resources.authorities.AppUser;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document
public class JobScript {
    
    @Id
    private String id;
    @NotBlank
    @Size(min = 3, max = 100)
    private String classFullName;
    @NotBlank
    private String extension;

    @NotNull
    @Valid
    private JobScriptDetails jobScriptDetails;

    @DBRef(lazy = true)
    @JsonFilter("job_script_jobs_files")
    @NotNull
    private List<JobsFile> jobsFiles = new LinkedList<>();
    
    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_project_reference")
    @NotNull
    private Project project;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_job_node_reference")
    @NotNull
    private JobNode jobNode;

    @DBRef(lazy = true)
    @JsonFilter("ilum_resource_publisher")
    @NotNull
    private AppUser author;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JobScript other = (JobScript) obj;
        return id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName() + "_" + id);
    }
}
