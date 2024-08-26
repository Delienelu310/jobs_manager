package com.ilumusecase.trading_bot;



import com.ilumusecase.annotations.processors.JobProcessor;
import com.ilumusecase.annotations.resources.InputChannel;
import com.ilumusecase.annotations.resources.JobNode;
import com.ilumusecase.annotations.resources.OutputChannel;



import cloud.ilum.job.Job;
import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.Tuple5;
import scala.collection.JavaConverters;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import org.apache.spark.api.java.function.Function;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;



@JobNode
public final class OptimalSignals implements Job{
    
    @InputChannel(label = "source")
    static public Dataset<Row> source;
    
    @InputChannel(label = "ATR")
    static public Dataset<Row> ATR;

    @OutputChannel(label = "signals")
    static public Dataset<Row> signals;


    static private class ProfitCalculatingAlgorithm implements Function<Row, Tuple5<String,Double,Double,String,String>>{


        private int listLimit = 0;

        public ProfitCalculatingAlgorithm(int listLimit){
            this.listLimit = listLimit;
        }

        private class RowData{
            String date;
            Double close;
            Double profit;
            public RowData(String date, Double close, Double profit) {
                this.date = date;
                this.close = close;
                this.profit = profit;
            }
        }

        private List<RowData> previousRows = new LinkedList<>();

        @Override
        public Tuple5<String,Double,Double,String,String> call(Row row) {

            Double currentClose = Double.parseDouble(row.getAs("Close"));
            String currentDate = row.getAs("Date");
            Double currentATR = Double.parseDouble(row.getAs("ATR"));

            //to calculate:
            String prevDate = currentDate;
            String prevAction = "hold";
            Double bestProfit = 1.0;

            for(RowData rowData : previousRows){
                Double profit = rowData.profit;
                Double difference = Math.abs(currentClose - rowData.close);
                if(2 * currentATR > difference){
                    difference = 0.0;
                }
                profit += difference * profit / rowData.close;

                if(profit > bestProfit){
                    bestProfit = profit;
                    prevDate = rowData.date;
                    if(difference == 0.0){
                        prevAction = "hold";
                    }else{
                        if(currentClose > rowData.close){
                            prevAction = "buy";
                        }else{
                            prevAction = "sell";
                        }
                    }
                }
            }

            if(previousRows.size() == listLimit){
                previousRows.remove(0);
            }
            previousRows.add(new RowData(currentDate, currentClose, bestProfit));

            return new Tuple5<String,Double,Double,String,String>(currentDate, currentClose, bestProfit, prevDate, prevAction);


        }

    }

    static private class SignalsSettingAlgorithm implements Function<Tuple5<String, Double, Double, String, String>, Row>{


        private String currentSignal = null;
        private String startDate = null;

        @Override
        public Row call(Tuple5<String, Double, Double, String, String> row) throws Exception {
            
            if(currentSignal == null){
                currentSignal = row._5();
                startDate = row._4();

                return Row.fromTuple(new Tuple2<String, String>(row._1(), "hold"));
            }

            if(startDate.equals(row._1())){

                String signal = currentSignal;

                startDate = row._4();
                currentSignal = row._5();
            
                return Row.fromTuple(new Tuple2<String, String>(row._1(), signal));
            
            }else{
                
                return Row.fromTuple(new Tuple2<String, String>(row._1(), currentSignal));
            }

        }

     
    }

    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {


        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(OptimalSignals.class, sparkSession, configJava);

        jobProcessor.start();

        // calcualte signals

        Dataset<Row> source = OptimalSignals.source.join(OptimalSignals.ATR, "Date");

        JavaRDD<Row> sourceRDD = source.javaRDD();
        JavaRDD<Row> resultRDD = sourceRDD
            .sortBy(row -> row.getAs("Date"), true, 0)
            .map(new OptimalSignals.ProfitCalculatingAlgorithm(200))
            .sortBy(row -> row._1(), false, 0)
            .map(new OptimalSignals.SignalsSettingAlgorithm());
        
        StructType schema = new StructType(new StructField[]{
            DataTypes.createStructField("Date", DataTypes.StringType, false),
            DataTypes.createStructField("Signal", DataTypes.StringType, false)
        });
        OptimalSignals.signals = sparkSession.createDataFrame(resultRDD, schema);

        jobProcessor.finish();


        return Some.apply("DONE");

    }



}
