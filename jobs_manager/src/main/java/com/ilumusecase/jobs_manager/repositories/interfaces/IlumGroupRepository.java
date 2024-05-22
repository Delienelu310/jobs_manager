package com.ilumusecase.jobs_manager.repositories.interfaces;

import com.ilumusecase.jobs_manager.resources.IlumGroup;

public interface IlumGroupRepository {
    
    public IlumGroup retrieveById(String id);
    public IlumGroup retrieveByIlumId(String id);
    public void deleteById(String id);

    public IlumGroup updageGroupFull(IlumGroup ilumGroup);

}
