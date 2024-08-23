package com.ilumusecase.annotations.processors;

import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.ilumusecase.annotations.processors.channel_processors.ChannelProcessor;
import com.ilumusecase.annotations.processors.channel_processors.KafkaChannelProcessor;
import com.ilumusecase.annotations.processors.channel_processors.MinioChannelProcessor;
import com.ilumusecase.annotations.resources.InputChannel;
import com.ilumusecase.annotations.resources.JobNode;
import com.ilumusecase.annotations.resources.JobNodeMod;
import com.ilumusecase.annotations.resources.OutputChannel;
import com.ilumusecase.data_supplier.DataSupplierClient;
import com.ilumusecase.resources.ChannelDTO;
import com.ilumusecase.resources.JobNodeDTO;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JobProcessor {

    private DataSupplierClient dataSupplierClient;
    
    private Map<String, ChannelProcessor> channelProcessors= new HashMap<>();
    {
        channelProcessors.put("KAFKA", new KafkaChannelProcessor());    
        channelProcessors.put("MINIO", new MinioChannelProcessor());
    }

    private JobNodeDTO jobNodeDTO;
    private Class<?> clazz;
    private SparkSession sparkSession;
    private Map<String, Object> config;


    public JobProcessor(Class<?> clazz, SparkSession session, Map<String, Object> config){

        String prefix = (String)config.get("prefix");
        this.dataSupplierClient = new DataSupplierClient(prefix);

        if(!clazz.isAnnotationPresent(JobNode.class)){
            throw new RuntimeException();
        }
        if(JobNodeMod.valueOf((String)config.get("mod")) != JobNodeMod.NORMAL){
            throw new RuntimeException();
        }

        String projectId = (String)config.get("projectId");
        String jobNodeId = (String)config.get("jobNodeId");
        String token = (String)config.get("token");
        

        this.clazz = clazz;
        this.sparkSession = session;
        this.config = config;
        
        // Access annotation values
        this.jobNodeDTO = null;
        try{
            this.jobNodeDTO = dataSupplierClient.retrieveJobNode(projectId, jobNodeId, token);
        }catch(Exception e){
            RuntimeException exception = new RuntimeException(e.getMessage());
            exception.setStackTrace(e.getStackTrace());
            System.out.println("to handle exception");
            throw exception;
        }
    }

    public void start(){
        
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            if( ! field.isAnnotationPresent(InputChannel.class)){
                continue;
            }

            InputChannel inputChannel = field.getAnnotation(InputChannel.class);
            
            List<Dataset<Row>> inputDatasets = new ArrayList<>();

            
        
            for(ChannelDTO channel : jobNodeDTO.input
                .get(inputChannel
                    .label())
                .channelList
            ){

                System.out.println("Trying to connect to channel : " + channel.id);

                ChannelProcessor channelProcessor = channelProcessors.get(channel.channelDetails.type);
                Dataset<Row> dataset = channelProcessor.retrieveInputDataSet(channel, this.sparkSession, config);
            
                inputDatasets.add(dataset);

                System.out.println("Added dataset: " + channel.id);
            }

            Dataset<Row> finalDataset = inputDatasets.stream().reduce( (ds1, ds2) -> ds1.union(ds2) ).get();

            Dataset<Row> finalDatasetCopy = finalDataset.select("*");

            //clear cache: 
            inputDatasets.stream().forEach(df -> df.unpersist());
            finalDataset.unpersist();

            System.out.println("Prepared dataset " + inputChannel.label());

            field.setAccessible(true);
            try{
                field.set(this.clazz, finalDataset);
            }catch(IllegalAccessException e){
                //do nothing
            }

        }
    }

    public void finish(){
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            if( ! field.isAnnotationPresent(OutputChannel.class)){
                continue;
            }

            OutputChannel outputChannel = field.getAnnotation(OutputChannel.class);
            
            Dataset<Row> dataset = null;
            field.setAccessible(true);
            try{
                dataset = (Dataset<Row>)(field.get(clazz));
            }catch(IllegalAccessException e){
                //do nothing
            }
            
            for(ChannelDTO channel : jobNodeDTO.output.get(outputChannel.label()).channelList){
                ChannelProcessor channelProcessor = channelProcessors.get(channel.channelDetails.type);
                
                try {
                    channelProcessor.connectToOutputChannel(channel, dataset, this.sparkSession, config);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            
            }

        }
    }
}
