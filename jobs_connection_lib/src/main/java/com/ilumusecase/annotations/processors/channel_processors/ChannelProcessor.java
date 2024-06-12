package com.ilumusecase.annotations.processors.channel_processors;

import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.ilumusecase.resources.ChannelDTO;

public interface ChannelProcessor {
    
    // temporary i used Object, in future replace object with DataSet<Row> or sparkSession
    public Dataset<Row> retrieveInputDataSet(ChannelDTO channelData, SparkSession session, Map<String, Object> config);
    public void connectToOutputChannel( ChannelDTO channelDTO, Dataset<Row> dataset, SparkSession session, Map<String, Object> config) throws Exception;

    public Dataset<Row> retrieveOutputDatasetFull(ChannelDTO channelData, SparkSession session, Map<String, Object> config);

}
