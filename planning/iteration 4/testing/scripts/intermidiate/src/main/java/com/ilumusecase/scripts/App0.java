package com.ilumusecase.scripts;

import org.apache.spark.sql.SparkSession;

import cloud.ilum.job.Job;
import scala.Option;
import scala.Some;
import scala.collection.immutable.Map;

public class App0 implements Job {

    @Override
    public Option<String> run(SparkSession sparkSession, Map<String, Object> config) {

        return Some.apply(sparkSession.version());
    }
    
}
