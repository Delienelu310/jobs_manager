package com.ilumusecase.trading_bot;



import com.ilumusecase.annotations.processors.JobProcessor;
import com.ilumusecase.annotations.resources.InputChannel;
import com.ilumusecase.annotations.resources.JobNode;
import com.ilumusecase.annotations.resources.OutputChannel;



import cloud.ilum.job.Job;
import scala.Option;
import scala.Some;
import scala.collection.JavaConverters;

import java.util.Map;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;



@JobNode
public final class App implements Job{
    
    @InputChannel(label = "ETHBTC")
    static public Dataset<Row> tradesHistory;
    
    @OutputChannel(label = "Signals")
    static public Dataset<Row> signals;

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {


        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(App.class, sparkSession, configJava);

        jobProcessor.start();

        int n = 14;

        //calculate ATR

        App.tradesHistory.createOrReplaceTempView("trade_history");

        final String calcMetrics = String.format(
            "SELECT " +
            "Date, Close, " +
            "ABS(High - Low) AS `H-L`, " +
            "ABS(High - LAG(Adj_Close, 1) OVER (ORDER BY Date)) AS `H-PC`, " +
            "ABS(Low - LAG(Adj_Close, 1) OVER (ORDER BY Date)) AS `L-PC`, " +
            "FROM trade_history"
        );

        Dataset<Row> tradesHisotryWithMetrics = sparkSession.sql(calcMetrics);
        tradesHisotryWithMetrics.createOrReplaceTempView("trade_history");


        final String calcTRQuery = "SELECT Date, Close, GREATEST( H-L, H-PC, L-POC ) as TR FROM trade_history";

        Dataset<Row> tradesHisotryWithTR = sparkSession.sql(calcTRQuery);
        tradesHisotryWithTR.createOrReplaceTempView("trade_history");


        final String calcATRQuery = String.format("Select Date, Close, " + 
            "AVG(TR) OVER (ORDER BY Date ROWS BETWEEN %d PRECEDING AND CURRENT ROW) AS ATR" + 
            "FROM trade_history",
            n-1
        );
        Dataset<Row> tradeHistoryWithATR = sparkSession.sql(calcATRQuery);
        tradeHistoryWithATR.createOrReplaceTempView("trade_history");


        // calcualte signals

        String calculateSignalsQuery = "";

        jobProcessor.finish();


        return Some.apply("DONE");

    }



}
