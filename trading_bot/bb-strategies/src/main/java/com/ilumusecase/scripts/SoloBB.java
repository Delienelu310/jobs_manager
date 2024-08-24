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
public final class SoloBB implements Job{

    @InputChannel(label = "BB")
    static public Dataset<Row> BB;
    @InputChannel(label = "source")
    static public Dataset<Row> source;
    @OutputChannel(label = "Signal")
    static public Dataset<Row> signals;

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(SoloBB.class, sparkSession, configJava);

        jobProcessor.start();

        Dataset<Row> source = SoloBB.source.join(SoloBB.BB, "Date");
        source.createOrReplaceTempView("source");

        SoloBB.signals = sparkSession.sql("Select Date, " + 
                " ( Case When UB < Close Then 'buy' " +
                    " When LB > Close Then 'sell' " + 
                    " Else 'hold' " +  
                " End ) as Signal" + 
            " From source"
        );

        jobProcessor.finish();

        return Some.apply("DONE");
    }
  
}
