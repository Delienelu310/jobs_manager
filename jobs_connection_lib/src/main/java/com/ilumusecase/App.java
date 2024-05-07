package com.ilumusecase;

import com.ilumusecase.annotations.processors.JobProcessor;
import com.ilumusecase.annotations.resources.InputChannel;
import com.ilumusecase.annotations.resources.JobNode;



import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

@JobNode(
    projectId = "663a3cf1507e6f2ba7f7d165",
    jobNodeId = "663a3d28507e6f2ba7f7d166"
)
public class App {

    @InputChannel(label = "xyz")
    private static Dataset<Row> input;

    public static void main( String[] args ) throws StreamingQueryException
    {
        System.setProperty("hadoop.home.dir", "c:/hadoop");	
		Logger.getLogger("org.apache").setLevel(Level.WARN);
		Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);

		SparkSession session = SparkSession.builder()
            .master("local[*]")
            .appName("structuredViewingReport")
            .getOrCreate()
        ;


        JobProcessor jobProcessor = new JobProcessor(App.class, session);
        jobProcessor.start();

        System.out.println(input.toString());

        StreamingQuery query = input
            .writeStream()
            .format("console")
            .outputMode(OutputMode.Append())
            .start();

        // jobProcessor.finish();

        query.awaitTermination();

        
    }
}
