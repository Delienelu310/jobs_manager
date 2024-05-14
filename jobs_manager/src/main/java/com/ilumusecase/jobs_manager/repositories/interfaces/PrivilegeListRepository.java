package com.ilumusecase.jobs_manager.repositories.interfaces;

import com.ilumusecase.jobs_manager.resources.PrivilegeList;

public interface PrivilegeListRepository<T> {
    public PrivilegeList<T> create();
    public PrivilegeList<T> update(PrivilegeList<T> channelList);
    public void delete(String id);
    
}
