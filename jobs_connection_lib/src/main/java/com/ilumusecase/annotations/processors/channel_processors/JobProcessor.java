package com.ilumusecase.annotations.processors.channel_processors;

import java.util.Map;
import java.util.HashMap;

public class JobProcessor {
    
    private Map<String, ChannelProcessor> channelProcessors= new HashMap<>();
    {
        channelProcessors.put("kafka", new KafkaChannelProcessor());    
    }


    public JobProcessor(Class<?> clazz, Object object){

    }

    public void start(){

    }

    public void finish(){

    }
}
