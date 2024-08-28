package com.ilumusecase.jobs_manager.resources.authorities;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class PrivilegeList<T>{

    @Id
    private String id;
    
    @NotNull
    @Valid
    private List<T> list = new LinkedList<>();
}
