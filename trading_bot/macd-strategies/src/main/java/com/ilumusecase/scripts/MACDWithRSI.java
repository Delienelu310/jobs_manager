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
public class MACDWithRSI implements Job{

    @InputChannel(label = "MACD")
    static public Dataset<Row> MACD;

    @InputChannel(label = "RenkoChart")
    static public Dataset<Row> RenkoChart;


    @OutputChannel(label = "Signal")
    static public Dataset<Row> signal;

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(MACDWithRSI.class, sparkSession, configJava);

        jobProcessor.start();

        Dataset<Row> source = MACDWithRSI.MACD.join(MACDWithRSI.RenkoChart, "Date");

        source.createOrReplaceTempView("source");

        MACDWithRSI.signal = sparkSession.sql("Select Date, " +
            " ( " +
                "Case When (Cast(BrickNumber as INT) >= 1 AND Cast(up as Boolean) AND Cast(MACD as Double) > Cast(signal_macd as Double)) Then " + 
                    " 'buy' " + 
                    " Else (Case When ( Cast(BrickNumber as INT) >= 1 AND not Cast(up as Boolean) AND Cast(MACD as Double) < Cast(signal_macd as Double) ) Then " + 
                        " 'sell' " + 
                        " Else 'hold' " + 
                    "End )"  + 
            " End ) as Signal" +         
            " From source"
        );

        jobProcessor.finish();

        return Some.apply("DONE");
    }



    
}
