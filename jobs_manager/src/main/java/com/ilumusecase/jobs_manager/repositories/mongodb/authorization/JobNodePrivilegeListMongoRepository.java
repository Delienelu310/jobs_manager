package com.ilumusecase.jobs_manager.repositories.mongodb.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.authorization.PrivilegeListRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.authorization.MongoJobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.JobNodePrivilege;
import com.ilumusecase.jobs_manager.resources.authorities.PrivilegeList;

@Repository
public class JobNodePrivilegeListMongoRepository implements PrivilegeListRepository<JobNodePrivilege>{

    @Autowired
    private MongoJobNodePrivilege mongoJobNodePrivilege;

    @Override
    public PrivilegeList<JobNodePrivilege> create() {
        return mongoJobNodePrivilege.save(new PrivilegeList<>());
    }

    @Override
    public PrivilegeList<JobNodePrivilege> update(PrivilegeList<JobNodePrivilege> channelList) {
        return mongoJobNodePrivilege.save(channelList);
    }

    @Override
    public void delete(String id) {
        mongoJobNodePrivilege.deleteById(id);
    }
}
