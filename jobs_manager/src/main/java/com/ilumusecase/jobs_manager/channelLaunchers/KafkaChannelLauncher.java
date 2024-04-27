package com.ilumusecase.jobs_manager.channelLaunchers;

import java.util.Collections;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import com.ilumusecase.jobs_manager.resources.Channel;

@Component("kafka")
public class KafkaChannelLauncher implements ChannelLauncher{
    
    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Override
    public void launchChannel(Channel channel) {
        
        short a = 1;
        NewTopic newTopic = new NewTopic("internal_" + channel.getId(), 3, a);
        kafkaAdmin.createOrModifyTopics(newTopic);
    }
    @Override
    public void stopChannel(Channel channel) {
       
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.deleteTopics(Collections.singletonList("internal_" + channel.getId()));
        }
    }

    
}
