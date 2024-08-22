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
public final class TechIndicators implements Job{

    @InputChannel(label = "source")
    static public Dataset<Row> source;

    @OutputChannel(label = "ATR")
    static public Dataset<Row> ATR;


    private Dataset<Row> calculateATR(Integer n, SparkSession session, Dataset<Row> source){
        source.createOrReplaceTempView("calculateATR_source");

        // source.show();

        final String calcMetrics = String.format(
            "SELECT " +
            "Date, Close, " +
            "ABS(High - Low) AS `HL`, " +
            "ABS(High - LAG('Adj Close', 1) OVER (ORDER BY Date)) AS `HPC`, " +
            "ABS(Low - LAG('Adj Close', 1) OVER (ORDER BY Date)) AS `LPC` " +
            "FROM calculateATR_source"
        );

        Dataset<Row> tradesHisotryWithMetrics = session.sql(calcMetrics);
        tradesHisotryWithMetrics.createOrReplaceTempView("calculateATR_before_tr");


        final String calcTRQuery = "SELECT Date, Close, GREATEST( HL, HPC, LPC ) as TR FROM calculateATR_before_tr";

        Dataset<Row> tradesHisotryWithTR = session.sql(calcTRQuery);
        tradesHisotryWithTR.createOrReplaceTempView("calculateATR_true_range");


        final String calcATRQuery = String.format("Select Date, Close, " + 
            "AVG(TR) OVER (ORDER BY Date ROWS BETWEEN %d PRECEDING AND CURRENT ROW) AS ATR " + 
            "FROM calculateATR_true_range",
            n-1
        );
        Dataset<Row> tradeHistoryWithATR = session.sql(calcATRQuery);
        tradeHistoryWithATR.createOrReplaceTempView("calculateATR_atr");

        tradeHistoryWithATR = session.sql("SELECT Date, ATR FROM calculateATR_atr");

        return tradeHistoryWithATR;
    }
    

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(TechIndicators.class, sparkSession, configJava);

        jobProcessor.start();

        TechIndicators.ATR = calculateATR(14, sparkSession, TechIndicators.source);

        jobProcessor.finish();

        return Some.apply("DONE");

    }
}
