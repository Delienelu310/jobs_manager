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
public class ChannelList {
    @Id
    private String id;

    @DBRef
    @JsonFilter("node-plug-channel")
    private List<Channel> channelList = new ArrayList<>();


}
