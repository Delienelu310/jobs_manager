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

        String name = "internal_" + channel.getId();
        
        // for(String label : channel.getProject().getInputChannels().keySet()){
        //     if(channel.getProject().getInputChannels().get(label).getId().equals(channel.getId())){
        //         name = "project_" + channel.getProject().getId() + "_input_" + label;
        //         break;
        //     }
        // }

        // for(String label : channel.getProject().getOutputChannels().keySet()){
        //     if(channel.getProject().getOutputChannels().get(label).getId().equals(channel.getId())){
        //         name = "project_" + channel.getProject().getId() + "_input_" + label;
        //         break;
        //     }
        // }

        NewTopic newTopic = new NewTopic(name, 3, a);
        kafkaAdmin.createOrModifyTopics(newTopic);
    }
    @Override
    public void stopChannel(Channel channel) {
       
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            String name = "internal_" + channel.getId();

            for(String label : channel.getProject().getInputChannels().keySet()){
                if(channel.getProject().getInputChannels().get(label).getId().equals(channel.getId())){
                    name = "project_" + channel.getProject().getId() + "_input_" + label;
                    break;
                }
            }
    
            for(String label : channel.getProject().getOutputChannels().keySet()){
                if(channel.getProject().getOutputChannels().get(label).getId().equals(channel.getId())){
                    name = "project_" + channel.getProject().getId() + "_input_" + label;
                    break;
                }
            }

            adminClient.deleteTopics(Collections.singletonList(name));
        }
    }

    
}
