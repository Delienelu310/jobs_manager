package com.ilumusecase.annotations.processors.channel_processors;

import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import com.ilumusecase.resources.ChannelDTO;

public class MinioChannelProcessor implements ChannelProcessor{

    private boolean isHeaderValid(String[] header, Dataset<Row> dataset){
        String[] actualHeaders = dataset.schema().fieldNames();

        if(header.length != actualHeaders.length){
            return false;
        }

        for(int i = 0; i < actualHeaders.length; i++){
            if( ! header[i].equals(actualHeaders[i]) ) return false;
        }


        return true;
    }

    @Override
    public Dataset<Row> retrieveInputDataSet(ChannelDTO channelData, SparkSession session, Map<String, Object> config) {

        // String bucketName = (String)config.get("minio_bucket");
        String bucketName = "ilum-files";

        Dataset<Row> dataset = session.read()
            .option("header", "true")
            .csv("s3a://" + bucketName + "/jobs-manager/internal_" + channelData.id + "/");

        if(!isHeaderValid(channelData.channelDetails.headers, dataset)){
            throw new RuntimeException("Invalid headers");
        }

        return dataset;
    }

    @Override
    public void connectToOutputChannel(ChannelDTO channelDTO, Dataset<Row> dataset, SparkSession session,
        Map<String, Object> config
    ) throws Exception {

        
        // String bucketName = (String)config.get("minio_bucket");
        String bucketName = "ilum-files";
        String filePath = "s3a://" + bucketName + "/jobs-manager/internal_" + channelDTO.id;

        dataset.coalesce(1).write()
            .option("header", "true")
            .mode(SaveMode.Overwrite)
            .csv(filePath);
    }

    @Override
    public Dataset<Row> retrieveOutputDatasetFull(ChannelDTO channelData, SparkSession session,
        Map<String, Object> config
    ) {
        // String bucketName = (String)config.get("minio_bucket");
        String bucketName = "ilum-files";

        Dataset<Row> dataset = session.read()
            .option("header", "true")
            .csv("s3a://" + bucketName + "/jobs-manager/internal_" + channelData.id + "/");

        if(!isHeaderValid(channelData.channelDetails.headers, dataset)){
            throw new RuntimeException("Invalid headers");
        }

        return dataset;
    }
    
}
