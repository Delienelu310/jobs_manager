package com.ilumusecase.jobs_manager.repositories.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.PrivilegeListRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoProjectPrivileges;
import com.ilumusecase.jobs_manager.resources.PrivilegeList;
import com.ilumusecase.jobs_manager.resources.ProjectPrivilege;

@Repository
public class ProjectPrivilegeListMongoRepository implements PrivilegeListRepository<ProjectPrivilege>{

    @Autowired
    private MongoProjectPrivileges mongoProjectPrivileges;

    @Override
    public PrivilegeList<ProjectPrivilege> create() {
        return mongoProjectPrivileges.save(new PrivilegeList<ProjectPrivilege>());
    }

    @Override
    public PrivilegeList<ProjectPrivilege> update(PrivilegeList<ProjectPrivilege> channelList) {
        return mongoProjectPrivileges.save(channelList);
    }

    @Override
    public void delete(String id) {
        mongoProjectPrivileges.deleteById(id);
    }
    
}
