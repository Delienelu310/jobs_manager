package com.ilumusecase.scripts;



import com.ilumusecase.annotations.processors.TestJobProcessor;
import com.ilumusecase.annotations.resources.InputChannel;
import com.ilumusecase.annotations.resources.OutputChannelTestDataset;
import com.ilumusecase.annotations.resources.TestJob;

import cloud.ilum.job.Job;
import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.collection.JavaConverters;

import java.util.Map;

import org.apache.spark.api.java.function.Function;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.max;
import static org.apache.spark.sql.functions.stddev;

@TestJob
public class PerformanceIndicators implements Job{


    @InputChannel(label="source")
    public static Dataset<Row> source;

    @OutputChannelTestDataset(label="Signal")
    public static Dataset<Row> signals;


    public static class CalculateProfitsFunction implements Function<Row, Row>{

        private String action = "hold";
        private Double bank = 1.0;
        private Double previousPrice = null;

        @Override
        public Row call(Row row) throws Exception {
            
            Double currentPrice = Double.parseDouble(row.getAs("Close"));

            if(previousPrice == null){
                previousPrice = currentPrice;
            }

            Double pctChange = (currentPrice - previousPrice) / previousPrice;
            
            switch(action){
                case "hold":
                    break;
                case "sell":
                    bank += ( - pctChange ) * bank * 0.1;
                    break;
                case "buy":
                    bank += pctChange * bank * 0.1;
                    break;
            }
            action = row.getAs("Signal");
            previousPrice = currentPrice;

            return Row.fromTuple(new Tuple2<String, Double>(row.getAs("Date"), bank));
        }

    }
  
    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        

        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        TestJobProcessor testJobProcessor = new TestJobProcessor(PerformanceIndicators.class, sparkSession, configJava);

        testJobProcessor.start();

        //1. calculate profits in strategy, where 10% of whole bank is used for trading

        Dataset<Row> source = PerformanceIndicators.source.select("Date", "Close").join(PerformanceIndicators.signals, "Date");
        


        Dataset<Row> bank = sparkSession.createDataFrame(
            source.toJavaRDD()
                .sortBy(r -> r.getAs("Date"), true, 0)
                .map(new PerformanceIndicators.CalculateProfitsFunction()),

                new StructType(new StructField[]{
                    DataTypes.createStructField("Date", DataTypes.StringType, false),
                    DataTypes.createStructField("Bank", DataTypes.DoubleType, false)
                })
        );
        bank.createOrReplaceTempView("bank");

        bank = sparkSession.sql("Select Date, Bank, " + 
            " (  (Bank - Lag(Bank, 1) Over (Order By Date)) / (Lag(Bank, 1) Over (Order By Date)) ) as ProcentChange" + 
            " From bank"
        );

        //calcualte CARG
        Double lastBank = (double)bank.orderBy(col("Date").desc()).limit(1).first().getAs("Bank");
        Double years = bank.count() * 1.0 / 365;
        Double CAGR = Math.pow(lastBank, 1.0 / years ) - 1.0;

        //calculate Sharpe ratio

        Double standardDeviation = bank.agg(stddev("ProcentChange").alias("pct_change_std"))
            .collectAsList().get(0).getAs("pct_change_std");
        final Double riskFreeAnnualProfit = 0.05;

        Double SharpeRatio = ( CAGR - riskFreeAnnualProfit) / standardDeviation; 


        //calculate calmar ratio:

        
        Double maxDropDown = sparkSession.sql("Select Date, Bank, " + 
            " ( ( ( Lag(Bank, 1) Over (Order By Date) )  - Bank) / (Lag(Bank, 1) Over (Order By Date)) ) as DropDown " + 
            " From bank"
        ).agg(max("DropDown").alias("MaxDropDown")).collectAsList().get(0).getAs("MaxDropDown");

        Double CalmarRatio = maxDropDown <= 0.0 ? Double.MAX_VALUE : CAGR / maxDropDown;


        //return metrics as json:

        String testResultJson = String.format("{ " + 
            " \"CAGR\": \"%f\" " +
            "," + 
            " \"SharpeRatio\": \"%f\" " + 
            "," +
            " \"CalmarRatio\": \"%f\"" +
        " }", CAGR, SharpeRatio, CalmarRatio);

        return Some.apply(testResultJson);
    }

}
