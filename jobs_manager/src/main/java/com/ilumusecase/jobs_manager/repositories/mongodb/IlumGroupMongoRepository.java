package com.ilumusecase.jobs_manager.repositories.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.IlumGroupRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoIlumGroup;
import com.ilumusecase.jobs_manager.resources.ilum.IlumGroup;

@Repository
public class IlumGroupMongoRepository implements IlumGroupRepository{

    @Autowired
    private MongoIlumGroup mongoIlumGroup;

    @Override
    public IlumGroup retrieveById(String id) {
        return mongoIlumGroup.findById(id).get();
    }

    @Override
    public IlumGroup retrieveByIlumId(String id) {
        return mongoIlumGroup.findByIlumId(id);
    }

    @Override
    public void deleteById(String id) {
        mongoIlumGroup.deleteById(id);
    }

    @Override
    public IlumGroup updageGroupFull(IlumGroup ilumGroup) {
        return mongoIlumGroup.save(ilumGroup);
    }
    
}
