package com.ilumusecase.scripts;

import org.apache.spark.sql.SparkSession;

import com.ilumusecase.annotations.processors.TestJobProcessor;
import com.ilumusecase.annotations.resources.TestJob;

import cloud.ilum.job.Job;


import scala.collection.JavaConverters;
import scala.Option;
import java.util.Map;
import scala.Some;


@TestJob
public class EmptyTest implements Job {

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        TestJobProcessor testJobProcessor = new TestJobProcessor(EmptyTest.class, sparkSession, configJava);

        testJobProcessor.start();


        return Some.apply("{ \"empty\":\"1\"}");
    }
    


}
