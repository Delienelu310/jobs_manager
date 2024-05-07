package com.ilumusecase.annotations.processors;

import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.ilumusecase.annotations.processors.channel_processors.ChannelProcessor;
import com.ilumusecase.annotations.processors.channel_processors.KafkaChannelProcessor;
import com.ilumusecase.annotations.resources.InputChannel;
import com.ilumusecase.annotations.resources.JobNode;
import com.ilumusecase.annotations.resources.OutputChannel;
import com.ilumusecase.data_supplier.DataSupplierClient;
import com.ilumusecase.resources.ChannelDTO;
import com.ilumusecase.resources.JobNodeDTO;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobProcessor {

    private DataSupplierClient dataSupplierClient = new DataSupplierClient();
    
    private Map<String, ChannelProcessor> channelProcessors= new HashMap<>();
    {
        channelProcessors.put("kafka", new KafkaChannelProcessor());    
    }

    private JobNodeDTO jobNodeDTO;
    private Class<?> clazz;
    private SparkSession sparkSession;


    public JobProcessor(Class<?> clazz, SparkSession session){
        if(!clazz.isAnnotationPresent(JobNode.class)){
            throw new RuntimeException();
        }

        this.clazz = clazz;
        this.sparkSession = session;

        JobNode annotation = clazz.getAnnotation(JobNode.class);

        // Access annotation values
        String projectId = annotation.projectId();
        String jobNodeId = annotation.jobNodeId();
        this.jobNodeDTO = null;
        try{
            this.jobNodeDTO = dataSupplierClient.retrieveJobNode(projectId, jobNodeId);
        }catch(Exception e){
            System.out.println("to handle exception");
            throw new RuntimeException();
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

            for(ChannelDTO channel : jobNodeDTO.input.get(inputChannel.label()).channelList){

                System.out.println("Trying to connect to channel : " + channel.id);

                ChannelProcessor channelProcessor = channelProcessors.get(channel.channelDetails.type);
                Dataset<Row> dataset = channelProcessor.retrieveInputDataSet(channel, this.sparkSession);
            
                inputDatasets.add(dataset);

                System.out.println("Added dataset: " + channel.id);
            }

            Dataset<Row> finalDataset = inputDatasets.stream().reduce( (ds1, ds2) -> ds1.union(ds2) ).get();

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
                    channelProcessor.connectToOutputChannel(channel, dataset, this.sparkSession);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            
            }

        }
    }
}