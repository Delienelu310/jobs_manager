package com.ilumusecase.annotations.processors.channel_processors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.ilumusecase.resources.ChannelDTO;

public interface ChannelProcessor {
    
    // temporary i used Object, in future replace object with DataSet<Row> or sparkSession
    public Dataset<Row> retrieveInputDataSet(ChannelDTO channelData, SparkSession session);
    public void connectToOutputChannel( ChannelDTO channelDTO, Dataset<Row> dataset, SparkSession session) throws Exception;

}
