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
public final class SoloMACD implements Job{

    @InputChannel(label = "MACD")
    static public Dataset<Row> MACD;

    @OutputChannel(label = "Signal")
    static public Dataset<Row> signal;


    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(SoloMACD.class, sparkSession, configJava);

        jobProcessor.start();

        SoloMACD.MACD.createOrReplaceTempView("source");

        SoloMACD.signal = sparkSession.sql("Select Date, " + 
            "(" + 
                " Case When (MACD > signal_macd AND MACD < 0) Then " + 
                    " 'buy' " + 
                " Else ( Case When (MACD < signal_macd AND MACD > 0 ) Then " + 
                    " 'sell' " + 
                    "Else 'hold'" + 
                " End )" +
                " End " + 
            ") as Signal " + 
            " From source"
        );

        jobProcessor.finish();

        return Some.apply("DONE");
    }
    

}
