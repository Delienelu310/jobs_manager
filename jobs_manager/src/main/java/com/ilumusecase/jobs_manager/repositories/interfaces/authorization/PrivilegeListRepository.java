package com.ilumusecase.jobs_manager.repositories.interfaces.authorization;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface PrivilegeListRepository<T> {
    public PrivilegeList<T> create();
    public PrivilegeList<T> update(@Valid @NotNull PrivilegeList<T> channelList);
    public void delete(String id);
    
}
