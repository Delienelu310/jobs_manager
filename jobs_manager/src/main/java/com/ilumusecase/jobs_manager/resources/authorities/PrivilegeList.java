package com.ilumusecase.jobs_manager.resources.authorities;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PrivilegeList<T>{

    @Id
    private String id;

    private List<T> list = new LinkedList<>();
}
