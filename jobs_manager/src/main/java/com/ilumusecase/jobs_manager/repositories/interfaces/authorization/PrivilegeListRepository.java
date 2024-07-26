package com.ilumusecase.jobs_manager.repositories.interfaces.authorization;

import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;

public interface PrivilegeListRepository<T> {
    public PrivilegeList<T> create();
    public PrivilegeList<T> update(PrivilegeList<T> channelList);
    public void delete(String id);
    
}
