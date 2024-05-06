package com.ilumusecase.annotations.processors.channel_processors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

import com.ilumusecase.resources.ChannelDTO;

public class KafkaChannelProcessor  implements ChannelProcessor{

    @Override
    public Dataset<Row> retrieveInputDataSet(ChannelDTO channelData, SparkSession session) {
        return session.readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("subscribe", channelData.id)
            .load();

    }

    @Override
    public void connectToOutputChannel(ChannelDTO channelDTO, Dataset<Row> dataset, SparkSession session) throws Exception{
        StreamingQuery query = dataset.writeStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "host1:port1,host2:port2")
            .option("topic", channelDTO.id)
            .start();

        query.awaitTermination();
            
    }

   
}
