package com.ilumusecase.annotations.processors.channel_processors;

import com.ilumusecase.resources.ChannelDTO;

public interface ChannelProcessor {
    
    // temporary i used Object, in future replace object with DataSet<Row> or sparkSession
    public Object retrieveInputDataSet(ChannelDTO channelData);
    public void connectToOutputChannel( ChannelDTO channelDTO, Object dataset);

}
