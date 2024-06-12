package com.ilumusecase.annotations.processors.channel_processors;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;

import com.ilumusecase.resources.ChannelDTO;

public class KafkaChannelProcessor  implements ChannelProcessor{

    @Override
    public Dataset<Row> retrieveInputDataSet(ChannelDTO channelData, SparkSession session, Map<String, Object> config){

        System.out.println("The chosen kafka channel: " + "internal-" + channelData.id);

        return session.readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("subscribe", "internal-" + channelData.id)
            .load();

    }

    @Override
    public void connectToOutputChannel(ChannelDTO channelDTO, Dataset<Row> dataset, SparkSession session, Map<String, Object> config) throws Exception{
        
        StreamingQuery query = dataset.writeStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("topic", "internal-" + channelDTO.id)
            .start();

        query.awaitTermination();

            
    }

    @Override
    public Dataset<Row> retrieveOutputDatasetFull(ChannelDTO channelData, SparkSession session, Map<String, Object> config) {
        String startTime = (String)config.get("startTime");
        String endTime = (String)config.get("endTime");
        String timeFormat = (String)config.get("timeFormat");
            
        Dataset<Row> dataset = session.read()
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("subscribe", "internal-" + channelData.id)
            .option("startingOffsets", "earliest")
            .option("endingOffsets", "latest")
            .load();
        return dataset
            .filter(dataset.col("timestamp").cast("long").between(
                LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern(timeFormat)).toEpochSecond(ZoneOffset.UTC),
                LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern(timeFormat)).toEpochSecond(ZoneOffset.UTC)
            ));

            
    
    }

   
}
