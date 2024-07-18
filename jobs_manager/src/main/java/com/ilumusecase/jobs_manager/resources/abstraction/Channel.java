package com.ilumusecase.jobs_manager.resources.abstraction;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class Channel {
    @Id
    private String id;

    private ChannelDetails channelDetails;

    
    @DBRef
    @JsonFilter("project-reference")
    private Project project;


    @DBRef
    @JsonFilter("channel-plug-jobNode")
    private List<JobNode> inputJobs = new ArrayList<>();
    
    @DBRef
    @JsonFilter("channel-plug-jobNode")
    private List<JobNode> outputJobs = new ArrayList<>();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Channel other = (Channel) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
