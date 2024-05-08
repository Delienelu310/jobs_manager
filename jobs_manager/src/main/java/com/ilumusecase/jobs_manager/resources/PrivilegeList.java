package com.ilumusecase.jobs_manager.resources;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class PrivilegeList<T>{
    @Id
    private String id;
    private List<T> list = new LinkedList<>();
}
