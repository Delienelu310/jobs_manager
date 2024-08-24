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
public class MACDWithADX implements Job{


    @InputChannel(label = "MACD")
    static public Dataset<Row> MACD;

    @InputChannel(label = "ADX")
    static public Dataset<Row> ADX;


    @OutputChannel(label = "Signal")
    static public Dataset<Row> signal;

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(MACDWithADX.class, sparkSession, configJava);

        jobProcessor.start();

        Dataset<Row> source = MACDWithADX.MACD.join(MACDWithADX.ADX, "Date");

        source.createOrReplaceTempView("source");


        MACDWithADX.signal = sparkSession.sql("Select Date, " + 
                "( Case When ADX < 25 Then " + 
                    " 'hold' " + 
                    " Else ( Case When MACD > signal_macd Then " + 
                        " 'buy' " +
                        " Else 'sell' " +  
                    " End ) " +
                " End) as Signal " + 
            " From source"
        );



        jobProcessor.finish();

        return Some.apply("DONE");
    }
    
}
