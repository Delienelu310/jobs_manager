package com.ilumusecase.annotations.processors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.ilumusecase.annotations.processors.channel_processors.ChannelProcessor;
import com.ilumusecase.annotations.processors.channel_processors.KafkaChannelProcessor;
import com.ilumusecase.annotations.resources.JobNodeMod;
import com.ilumusecase.annotations.resources.OutputChannelTestDataset;
import com.ilumusecase.annotations.resources.TestJob;
import com.ilumusecase.data_supplier.DataSupplierClient;
import com.ilumusecase.resources.ChannelDTO;
import com.ilumusecase.resources.JobNodeDTO;

public class TestJobProcessor {
    private DataSupplierClient dataSupplierClient = new DataSupplierClient();
    
    private Map<String, ChannelProcessor> channelProcessors= new HashMap<>();
    {
        channelProcessors.put("kafka", new KafkaChannelProcessor());    
    }

    private JobNodeDTO jobNodeDTO;
    private Class<?> clazz;
    private SparkSession sparkSession;
    private Map<String, Object> config;


    public TestJobProcessor(Class<?> clazz, SparkSession session, Map<String, Object> config){
        if(!clazz.isAnnotationPresent(TestJob.class)){
            throw new RuntimeException();
        }
        if((JobNodeMod)config.get("mod") != JobNodeMod.TEST){
            throw new RuntimeException();
        }

        String projectId = (String)config.get("projectId");
        String jobNodeId = (String)config.get("jobNodeId");


        this.clazz = clazz;
        this.sparkSession = session;
        this.config = config;

        
        // Access annotation values
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
            if( ! field.isAnnotationPresent(OutputChannelTestDataset.class)){
                continue;
            }

            OutputChannelTestDataset outputChannel = field.getAnnotation(OutputChannelTestDataset.class);
            
            List<Dataset<Row>> datasets = new ArrayList<>();

            
        
            for(ChannelDTO channel : jobNodeDTO.output.get(outputChannel.label()).channelList){

                System.out.println("Trying to connect to channel : " + channel.id);

                ChannelProcessor channelProcessor = channelProcessors.get(channel.channelDetails.type);
                Dataset<Row> dataset = channelProcessor.retrieveOutputDatasetFull(channel, this.sparkSession, config);
            
                datasets.add(dataset);

                System.out.println("Added dataset: " + channel.id);
            }

            Dataset<Row> finalDataset = datasets.stream().reduce( (ds1, ds2) -> ds1.union(ds2) ).get();

            System.out.println("Prepared dataset " + outputChannel.label());

            field.setAccessible(true);
            try{
                field.set(this.clazz, finalDataset);
            }catch(IllegalAccessException e){
                //do nothing
            }

        }
    }
}
