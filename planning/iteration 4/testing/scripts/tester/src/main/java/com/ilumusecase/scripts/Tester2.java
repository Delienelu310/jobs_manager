package com.ilumusecase.scripts;

import cloud.ilum.job.Job;
import scala.Option;
import scala.Some;
import org.apache.spark.sql.SparkSession;


public class Tester2 implements Job{


    public static void main(String[] args) {

    }

   
    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> cfg) {
        
        

        throw new RuntimeException("Some tester exception example");


    }
}
