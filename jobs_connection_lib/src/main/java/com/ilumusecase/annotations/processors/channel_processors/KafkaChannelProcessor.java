package com.ilumusecase.annotations.processors.channel_processors;

import com.ilumusecase.resources.ChannelDTO;

public class KafkaChannelProcessor  implements ChannelProcessor{

    @Override
    public Object retrieveInputDataSet(ChannelDTO channelData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retrieveInputDataSet'");
    }

    @Override
    public void connectToOutputChannel(ChannelDTO channelDTO, Object dataset) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connectToOutputChannel'");
    }
    
}
