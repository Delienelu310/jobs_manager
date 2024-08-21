package com.ilumusecase.jobs_manager.repositories.interfaces.ilum;

import org.springframework.validation.annotation.Validated;

import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface IlumGroupRepository {
    
    public IlumGroup retrieveById(String id);
    public IlumGroup retrieveByIlumId(String id);
    public void deleteById(String id);

    public IlumGroup updageGroupFull(@Valid @NotNull IlumGroup ilumGroup);

}
