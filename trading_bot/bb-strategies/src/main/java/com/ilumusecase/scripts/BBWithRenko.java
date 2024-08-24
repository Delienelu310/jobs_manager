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
public class BBWithRenko implements Job {

    @InputChannel(label = "BB")
    static public Dataset<Row> BB;
    @InputChannel(label = "source")
    static public Dataset<Row> source;
    @InputChannel(label = "RenkoCharts")
    static public Dataset<Row> RenkoCharts;

    @OutputChannel(label = "Signal")
    static public Dataset<Row> signals;

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(BBWithRenko.class, sparkSession, configJava);

        jobProcessor.start();

        Dataset<Row> source = BBWithRenko.source.join(BBWithRenko.BB, "Date").join(BBWithRenko.RenkoCharts, "Date");

        source.createOrReplaceTempView("source");

        BBWithRenko.signals = sparkSession.sql("Select Date, " + 
                " ( Case When ( Cast(UB as Double) < Cast(Close as Doulbe) AND Cast(BrickNumber as Double) < 1) Then 'buy' " +
                    " When (Cast(LB as Double) > Cast(Close as Double) AND Cast(BrickNumber as Double) < 1) Then 'sell' " + 
                    " Else 'hold' " +  
                " End ) as Signal" + 
            " From source"
        );

        jobProcessor.finish();

        return Some.apply("DONE");
    }
}
