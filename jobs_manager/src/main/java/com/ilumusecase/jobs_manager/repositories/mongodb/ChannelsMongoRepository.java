package com.ilumusecase.jobs_manager.repositories.mongodb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ilumusecase.jobs_manager.repositories.interfaces.ChannelsRepository;
import com.ilumusecase.jobs_manager.repositories.mongodb.mongorepositories.MongoChannel;
import com.ilumusecase.jobs_manager.resources.Channel;
import com.ilumusecase.jobs_manager.resources.ChannelDetails;
import com.ilumusecase.jobs_manager.resources.Project;

@Repository
public class ChannelsMongoRepository implements ChannelsRepository{

    @Autowired
    private MongoChannel mongoChannel;

    @Override
    public Channel retrieveById(String id) {
        return mongoChannel.findById(id).get();
    }

    @Override
    public List<Channel> retrieveAll() {
        return mongoChannel.findAll();
    }

    @Override
    public List<Channel> retrieveAllByProjectId(String projectId) {
        return mongoChannel.findByProjectId(projectId);
    }

    @Override
    public Channel createChannel(Project project, ChannelDetails channelDetails) {
        Channel channel = new Channel();
        channel.setChannelDetails(channelDetails);
        channel.setProject(project);

        return mongoChannel.save(channel);
    }

    @Override
    public Channel updateChannel(String id, ChannelDetails channelDetails) {
        Channel channel = mongoChannel.findById(id).get();
        channel.setChannelDetails(channelDetails);

        return mongoChannel.save(channel);
    }

    @Override
    public Channel updateChannelFull(Channel channel) {
        return mongoChannel.save(channel);
    }

    @Override
    public void deleteChannelById(String id) {
        mongoChannel.deleteById(id);
    }
    
}
