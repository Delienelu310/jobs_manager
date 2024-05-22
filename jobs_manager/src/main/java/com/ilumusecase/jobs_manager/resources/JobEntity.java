package com.ilumusecase.jobs_manager.resources;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class JobEntity {

    @Id
    private String id;

    private String groupId;
    private String ilumId;
    private String extension;
    private String classPath;

    private String state;

    private JobDetails jobDetails;
    private JobResult jobResult;

    @DBRef
    private JobNode jobNode;
    @DBRef
    private Project project;
    @DBRef 
    private AppUser author;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JobEntity other = (JobEntity) obj;
        return id == other.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
