package com.ilumusecase.jobs_manager.repositories.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.ChannelListRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoChannelList;
import com.ilumusecase.jobs_manager.resources.abstraction.ChannelList;

@Repository
public class ChannelListMongoRepository implements ChannelListRepository{

    @Autowired
    private MongoChannelList mongoChannelList;

    @Override
    public ChannelList update(ChannelList channelList) {
        return mongoChannelList.save(channelList);
    }

    @Override
    public ChannelList create() {
        return mongoChannelList.save(new ChannelList());
    }

    @Override
    public void delete(String id) {
        mongoChannelList.deleteById(id);
    }
    
}
