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
public class RSIWithRenko implements Job{

    @InputChannel(label = "RSI")
    static public Dataset<Row> RSI;
    @InputChannel(label = "RenkoCharts")
    static public Dataset<Row> RenkoCharts;

    @OutputChannel(label = "Signal")
    static public Dataset<Row> signals;

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(RSIWithRenko.class, sparkSession, configJava);

        jobProcessor.start();

        Dataset<Row> source = RSIWithRenko.RSI.join(RSIWithRenko.RenkoCharts, "Date");
    
        source.createOrReplaceTempView("source");

        RSIWithRenko.signals = sparkSession.sql("Select Date, " + 
                "( Case When (Cast(BrickNumber as INT) < 1 AND Cast(RSI as Double) > 70) Then 'sell' " +
                    " When ( Cast(BrickNumber as INT) < 1  AND Cast(RSI as Double) < 30) Then 'buy' " + 
                    " Else 'hold' " +  
                " End ) as Signal " +
            " From source"
        );

        jobProcessor.finish();

        return Some.apply("DONE");
    }
    
}
