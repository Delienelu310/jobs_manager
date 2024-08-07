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

// import org.apache.log4j.Level;
// import org.apache.log4j.Logger;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;



@JobNode
public final class App2 implements Job {

    @InputChannel(label="digit")
    public static Dataset<Row> input;
    
    @OutputChannel(label="evaluation")
    public static Dataset<Row> output;

    public static void main(String[] args) {
        
    }

    static class Evaluator implements UDF1<String, String>{
        @Override
        public String call(String num) throws Exception {
            int digit = Integer.parseInt(num);

            if(digit >= 10){
                return "high";
            }else if(digit >= 5){
                return "middle";
            }else{
                return "small";
            }
        }
            
    }

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        // Logger.getLogger("org.apache").setLevel(Level.WARN);
		// Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);

		// SparkSession session = SparkSession.builder()
        //     .master("local[*]")
        //     .appName("structuredViewingReport")
        //     .getOrCreate()
        // ;



        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();
        // configJava.put("projectId", "663a3cf1507e6f2ba7f7d165");
        // configJava.put("jobNodeId", "667282bc0dad3166a3a098f2");
        // configJava.put("token", "Basic YWRtaW46YWRtaW4=");
        // configJava.put("mod", "NORMAL");

        JobProcessor jobProcessor = new JobProcessor(App2.class, sparkSession, configJava);
        System.out.println("STARTING");
        jobProcessor.start();
        System.out.println("STARTED ");

        sparkSession.udf().register("evaluate", new App2.Evaluator(), DataTypes.StringType);
        
        try {
            App2.input.createTempView("MyDigits2");
        } catch (AnalysisException e) {
            e.printStackTrace();
        }

        App2.output = sparkSession.sql("SELECT number, evaluate(number) as somedata FROM MyDigits2");

        jobProcessor.finish();

        return Some.apply("DONE");
    }
}
