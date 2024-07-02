package com.ilumusecase.annotations.processors.channel_processors;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQuery;

import com.ilumusecase.resources.ChannelDTO;

public class KafkaChannelProcessor  implements ChannelProcessor{

    public Dataset<Row> convertCSVToColumns(Dataset<Row> dataset, String[] headers) {

        dataset = dataset.withColumn("fields", functions.split(dataset.col("value"), ","));
        dataset = dataset.drop("value");

        for (int i = 0; i < headers.length; i++) {
            final int index = i; 
            dataset = dataset.withColumn(headers[i], dataset.col("fields").getItem(index));
        }

        dataset = dataset.drop("fields");

        return dataset;
    }

    public Dataset<Row> collapseColumnsToCSV(Dataset<Row> dataset) {

        String[] columns = dataset.columns();
        Column column = dataset.col(columns[0]);
        for(int i = 1; i < columns.length; i++){
            column = functions.concat_ws(",", column, dataset.col(columns[i]) );
        }

        dataset = dataset.withColumn("value", column);
        dataset = dataset.select("value");

        return dataset;
    }

    @Override
    public Dataset<Row> retrieveInputDataSet(ChannelDTO channelData, SparkSession session, Map<String, Object> config){

        String bootstrapServer = (String)config.get("bootstrapServer");

        System.out.println("The chosen kafka channel: " + "internal_" + channelData.id);

        Dataset<Row> dataset =  session.readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", bootstrapServer)
            .option("subscribe", "internal_" + channelData.id)
            .load();

        dataset.createOrReplaceTempView("Channel" + channelData.id);

        dataset = session.sql("SELECT CAST(value AS STRING) FROM Channel" + channelData.id);

        dataset = convertCSVToColumns(dataset, channelData.channelDetails.headers);

        return dataset;
    }

    @Override
    public void connectToOutputChannel(ChannelDTO channelDTO, Dataset<Row> dataset, SparkSession session, Map<String, Object> config) throws Exception{
        


        String bootstrapServer = (String)config.get("bootstrapServer");
        dataset = collapseColumnsToCSV(dataset);

        StreamingQuery query = dataset.writeStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", bootstrapServer)
            .option("topic", "internal_" + channelDTO.id)
            .option("checkpointLocation", "/tmp/spark/checkpoints/" + channelDTO.id)
            .start();

        query.awaitTermination();

            
    }

    @Override
    public Dataset<Row> retrieveOutputDatasetFull(ChannelDTO channelData, SparkSession session, Map<String, Object> config) {

        String bootstrapServer = (String)config.get("bootstrapServer");
        String startTime = (String)config.get("startTime");
        String endTime = (String)config.get("endTime");
        String timeFormat = (String)config.get("timeFormat");
            
        Dataset<Row> dataset = session.read()
            .format("kafka")
            .option("kafka.bootstrap.servers", bootstrapServer)
            .option("subscribe", "internal_" + channelData.id)
            .option("startingOffsets", "earliest")
            .option("endingOffsets", "latest")
            .load();
        dataset = dataset
            .filter(dataset.col("timestamp").cast("long").between(
                LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern(timeFormat)).toEpochSecond(ZoneOffset.UTC),
                LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern(timeFormat)).toEpochSecond(ZoneOffset.UTC)
            ));

        dataset.createOrReplaceTempView("Channel" + channelData.id);

        dataset = session.sql("SELECT CAST(value AS STRING) FROM Channel" + channelData.id);

        dataset = convertCSVToColumns(dataset, channelData.channelDetails.headers);

        return dataset;
        
    
    }

   
}
