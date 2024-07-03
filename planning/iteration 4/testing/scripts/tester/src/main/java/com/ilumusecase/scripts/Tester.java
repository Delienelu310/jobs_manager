package com.ilumusecase.scripts;

import com.ilumusecase.annotations.processors.TestJobProcessor;
import com.ilumusecase.annotations.resources.OutputChannelTestDataset;
import com.ilumusecase.annotations.resources.TestJob;

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
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.api.java.UDF2;

@TestJob
public final class Tester implements Job{
  
    @OutputChannelTestDataset(label = "evaluation")
    public static Dataset<Row> output;

    public static void main(String[] args) {
        

        


    }

    static class Evaluator implements UDF2<String, String, Boolean>{

        @Override
        public Boolean call(String num, String evaluation) throws Exception {
        
            int digit = Integer.parseInt(num);

            if(digit >= 8){
                return evaluation.equals("high");
            }else if(digit >= 4){
                return evaluation.equals("middle");
            }else{
                return evaluation.equals("small");
            }
        }
        
    }; 


    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> cfg) {
        
        // Logger.getLogger("org.apache").setLevel(Level.WARN);
		// Logger.getLogger("org.apache.spark.storage").setLevel(Level.ERROR);

		// SparkSession session = SparkSession.builder()
        //     .master("local[*]")
        //     .appName("structuredViewingReport")
        //     .getOrCreate()
        // ;

        Map<String, Object> config = JavaConverters.mapAsJavaMapConverter(cfg).asJava();

        // Map<String, Object> config = new HashMap<>();
        // config.put("projectId", "663a3cf1507e6f2ba7f7d165");
        // config.put("jobNodeId", "667282bc0dad3166a3a098f2");
        // config.put("token", "Basic YWRtaW46YWRtaW4=");
        // config.put("mod", "TEST");

        // String startTime = "2024-06-12 10:49:00.000";
        // String endTime= "2024-06-25 10:49:59.999";
        // String timeFormat = "yyyy-MM-dd HH:mm:ss.SSS";
        
        // config.put("startTime", startTime);
        // config.put("endTime", endTime);
        // config.put("timeFormat", timeFormat);
        TestJobProcessor jobProcessor = new TestJobProcessor(Tester.class, sparkSession, config);
        jobProcessor.start();


        try {
            Tester.output.createTempView("Tester_evaluation");
        } catch (AnalysisException e) {
            e.printStackTrace();
        }


        sparkSession.udf().register("checkEvaluation", new Tester.Evaluator(), DataTypes.BooleanType);

        Dataset<Row> result = sparkSession.sql("SELECT (SELECT COUNT(*) FROM Tester_evaluation WHERE checkEvaluation(number, somedata)) / (SELECT COUNT(*) FROM Tester_evaluation) FROM (SELECT COUNT(*) FROM Tester_evaluation) ");

        Double res = (Double)(result.collectAsList().get(0).get(0));
        System.out.println(res);

        return Some.apply(Double.toString(res));
    }
}
