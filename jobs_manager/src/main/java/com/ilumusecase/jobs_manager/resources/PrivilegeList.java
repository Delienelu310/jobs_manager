package com.ilumusecase.jobs_manager.resources;

import java.util.LinkedList;
import java.util.List;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PrivilegeList<T>{

    private List<T> list = new LinkedList<>();
}
