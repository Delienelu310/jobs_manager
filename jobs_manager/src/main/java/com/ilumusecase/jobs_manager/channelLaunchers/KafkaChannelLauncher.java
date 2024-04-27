package com.ilumusecase.jobs_manager.channelLaunchers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;

import com.ilumusecase.jobs_manager.resources.Channel;

public class KafkaChannelLauncher implements ChannelLauncher{
    

    private KafkaAdmin kafkaAdmin;
    {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaAdmin = new KafkaAdmin(configs);
    }
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
