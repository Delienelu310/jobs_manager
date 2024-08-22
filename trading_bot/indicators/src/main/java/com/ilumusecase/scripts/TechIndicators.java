package com.ilumusecase.scripts;

import com.ilumusecase.annotations.processors.JobProcessor;
import com.ilumusecase.annotations.resources.InputChannel;
import com.ilumusecase.annotations.resources.JobNode;
import com.ilumusecase.annotations.resources.OutputChannel;

import cloud.ilum.job.Job;
import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.Tuple4;
import scala.collection.JavaConverters;

import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;


@JobNode
public final class TechIndicators implements Job{

    @InputChannel(label = "source")
    static public Dataset<Row> source;

    @OutputChannel(label = "ATR")
    static public Dataset<Row> ATR;

    @OutputChannel(label = "MACD")
    static public Dataset<Row> MACD;

    @OutputChannel(label = "RSI")
    static public Dataset<Row> RSI;

    @OutputChannel(label ="BollBands")
    static public Dataset<Row> BB;

    @OutputChannel(label = "RenkoChart")
    static public Dataset<Row> RenkoChart;


    private static class EWMFunction implements Function<Row, Row>{

        
        private String sourceColumn;
        private String orderByColumn;
        private Double alpha;

        private Double previousEWM = null;

        

        public EWMFunction(String sourceColumn, String orderByColumn, Double alpha) {
            this.sourceColumn = sourceColumn;
            this.orderByColumn = orderByColumn;
            this.alpha = alpha;
        }

        @Override
        public Row call(Row currentRow) throws Exception {

            
            Object val = currentRow.getAs(sourceColumn);
        
            
            Double currentValue = 0.0;
            if(val instanceof String){
                try{
                    currentValue = Double.parseDouble((String)val);
                }catch(Exception e){

                }
            }else{
                currentValue = (double)val;
            }
            

            Double currentEwm = previousEWM == null ? currentValue : (currentValue * alpha + (1.0 - alpha) * previousEWM);
            
            previousEWM = currentEwm;
            Row result = Row.fromTuple(new Tuple2<String,Double>(currentRow.getAs(orderByColumn), currentEwm));
            
            return result;
        }
        
    }

    private Dataset<Row> calculateExponentialWeight(
        SparkSession session, 
        Dataset<Row> source, 
        double alpha, 
        String sourceColumn, 
        String targetColumn, 
        String orderByColumn
    ){

        JavaRDD<Row> sourceRDD = source.toJavaRDD().sortBy(row -> row.getAs(orderByColumn), true, 0);

        JavaRDD<Row> ewmRDD = sourceRDD.map(new EWMFunction(sourceColumn, orderByColumn, alpha));


        StructField orderByField = null;
        for(StructField field :  source.schema().fields()){
            if(field.name().equals(orderByColumn)){
                orderByField = field;
                break;
            }
        }

        StructType schema = new StructType(new StructField[]{
            orderByField,
            DataTypes.createStructField(targetColumn, DataTypes.DoubleType, true),
        });
        Dataset<Row> ewm = session.createDataFrame(ewmRDD, schema);

        return source.join(ewm, orderByColumn);

    }

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
    

    private Dataset<Row> calculateMACD(SparkSession session, Dataset<Row> source, int a, int b, int c){

        double maFastAlphaSpan = 2.0 / (1 + a);
        double maSlowAlphaSpan = 2.0 / (1 + b);
        source.printSchema();
        source = calculateExponentialWeight(session, source, maFastAlphaSpan, "Adj Close", "ma_fast", "Date");
        source.printSchema();
        source = calculateExponentialWeight(session, source, maSlowAlphaSpan, "Adj Close", "ma_slow", "Date");

        source.createOrReplaceTempView("source");


        Dataset<Row> result = session.sql("Select Date, (ma_fast - ma_slow) as MACD FROM source");

        double macdSignalAlphaSpan = 2.0 / (1 + c);
        result.printSchema();
        result = calculateExponentialWeight(session, result, macdSignalAlphaSpan, "MACD", "signal_macd", "Date");

        return result;
    }

    private Dataset<Row> calculateRSI(SparkSession session, Dataset<Row> source, int n){
    
        source.createOrReplaceTempView("source");

        Dataset<Row> withChange = session.sql("SELECT Date," + 
            " (`Adj Close` - LAG(`Adj Close`, 1) OVER (Order By Date)) as Change From source "
        );
        withChange.createOrReplaceTempView("change");

        withChange.show();

        Dataset<Row> withGainLoss = session.sql("SELECT Date," +
            " (Case When Change > 0 Then Change Else 0 END) as Gain, " + 
            "(Case When Change < 0 Then -Change Else 0 END) as Loss " + 
            " From change"
        );

        withGainLoss.show();
        
        withGainLoss = calculateExponentialWeight(session, withGainLoss, 1.0 / n, "Gain", "AvgGain", "Date");
        withGainLoss = calculateExponentialWeight(session, withGainLoss, 1.0 / n, "Loss", "AvgLoss", "Date");
        withGainLoss.createOrReplaceTempView("gainloss");


        Dataset<Row> result = session.sql("Select Date, " + 
            "(Case When AvgLoss = 0 Then 100 Else (100 - 100 / ( 1 + AvgGain / AvgLoss ) ) END) as RSI " + 
            " From gainloss"
        );
        return result;

    }

    private Dataset<Row> calculateBollBands(SparkSession session, Dataset<Row> source, int n){

        source.createOrReplaceTempView("source");

        Dataset<Row> intermidiate = session.sql(String.format(
            "Select Date, " + 
            "( Avg(`Adj Close`) Over (Order By Date Rows Between %d preceding and current row)) as Mean, " +
            "( STDDEV_SAMP(`Adj Close`) Over (Order By Date Rows Between %d preceding and current row)) as Std " +  
            " From source",
            n - 1, n - 1
        ));
        intermidiate.createOrReplaceTempView("intermidiate");

        return session.sql("Select Date, (Mean + 2 * Std) as UB, (Mean - 2 * Std) as LB, (4 * Std) as BBW From intermidiate");
        

    }


    public static class RenkoChartMapping implements Function<Row, Row>{


        private Double size;
        private String priceColumn;
        private String orderByColumn;

        public RenkoChartMapping(Double size, String priceColumn, String orderByColumn) {
            this.size = size;
            this.priceColumn = priceColumn;
            this.orderByColumn = orderByColumn;
        }

        private int brickNumber = 0;
        private Double openPrice = null; 
        private boolean up = true;

        @Override
        public Row call(Row row) throws Exception {
            
            //retrieve current price from row 
            Double currentPrice = 0.0;

            Object price = row.getAs(priceColumn);
            if(price instanceof String){
                try{
                    currentPrice = Double.parseDouble((String)price);
                }catch(Exception e){
                    currentPrice = openPrice;
                }
                
            }else{
                currentPrice = (double)price;
            }

            if(openPrice == null && currentPrice == null){
                return Row.fromTuple(new Tuple4<String, Boolean, Integer, Double>(
                    row.getAs(orderByColumn),
                    up,
                    brickNumber,
                    0.0
                ));
            }

            if(openPrice == null) openPrice = currentPrice;

            if(up && currentPrice > openPrice || !up && currentPrice < openPrice){

                brickNumber += Math.floor((currentPrice - openPrice) / size);
                openPrice += (up ? (+1) : (-1)) *  Math.floor((currentPrice - openPrice) / size) * size;

                
            }else{
                double difference = Math.abs(currentPrice - openPrice);
                if(difference > size){
                    up = !up;

                    brickNumber = (int)Math.floor(difference / size) - 1;
                    openPrice += (up ? 1 : -1) * Math.floor(difference / size) * size;
                }
            }

            return Row.fromTuple(new Tuple4<String, Boolean, Integer, Double>(
                row.getAs(orderByColumn),
                up,
                brickNumber,
                openPrice
            ));


        }

    }

    private Dataset<Row> calculateRenkoChart(SparkSession session, Dataset<Row> source, Double size){
        //result: Date, direction, open_price, number of brick

        JavaRDD<Row> sourceRDD = source.toJavaRDD();

        JavaRDD<Row> resultRDD = sourceRDD
            .sortBy((row) -> row.getAs("Date"), true, 0)
            .map(new TechIndicators.RenkoChartMapping(size, "Close", "Date"));


        StructType schema = new StructType(new StructField[]{
            DataTypes.createStructField("Date", DataTypes.StringType, false),
            DataTypes.createStructField("Up", DataTypes.BooleanType, false),
            DataTypes.createStructField("BrickNumber", DataTypes.IntegerType, false),
            DataTypes.createStructField("OpenPrice", DataTypes.DoubleType, true),
        });
        Dataset<Row> result = session.createDataFrame(resultRDD, schema);

        return result;
    
    }


    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        
        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(TechIndicators.class, sparkSession, configJava);

        jobProcessor.start();

        TechIndicators.ATR = calculateATR(14, sparkSession, TechIndicators.source);
        TechIndicators.MACD = calculateMACD(sparkSession, TechIndicators.source, 12, 26, 9);
        TechIndicators.RSI = calculateRSI(sparkSession, TechIndicators.source, 14);
        TechIndicators.BB = calculateBollBands(sparkSession, TechIndicators.source, 14);


        //get the brick size for renko: 
        Dataset<Row> atrForRenko = calculateATR(120, sparkSession, source);
        Row lastRow = atrForRenko.orderBy(atrForRenko.col("Date").desc()).limit(1).first();
        Double brickSize = 3 * (double)lastRow.getAs("ATR");

        TechIndicators.RenkoChart = calculateRenkoChart(sparkSession, source, brickSize);


        jobProcessor.finish();

        return Some.apply("DONE");

    }
}
