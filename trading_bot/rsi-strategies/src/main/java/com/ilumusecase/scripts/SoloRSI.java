package com.ilumusecase.scripts;

import com.ilumusecase.annotations.processors.JobProcessor;
import com.ilumusecase.annotations.resources.InputChannel;
import com.ilumusecase.annotations.resources.JobNode;
import com.ilumusecase.annotations.resources.OutputChannel;

import cloud.ilum.job.Job;
import scala.Option;
import scala.Some;
import scala.collection.JavaConverters;

import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

@JobNode
public final class SoloRSI implements Job {


    @InputChannel(label = "RSI")
    static public Dataset<Row> RSI;
    @OutputChannel(label = "Signal")
    static public Dataset<Row> signals;

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(SoloRSI.class, sparkSession, configJava);

        jobProcessor.start();

        SoloRSI.RSI.createOrReplaceTempView("source");

        SoloRSI.signals = sparkSession.sql("Select Date, " + 
                "( Case When Cast(RSI as Double) > 70 Then " + 
                    " 'sell' " +
                    "Else ( Case When Cast(RSI as Double) > 40 Then " + 
                        " 'hold' " + 
                        " Else 'buy' " + 
                    " End ) " + 
                " End ) as Signal " +        
            " From source"
        );

        jobProcessor.finish();

        return Some.apply("DONE");
    }
   
}
