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

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

@JobNode
public final class LinearSignalMLModel implements Job {


    @InputChannel(label="MACD_signal")
    static public Dataset<Row> macdSignal;

    @InputChannel(label="RSI_signal")
    static public Dataset<Row> rsiSignal;

    @InputChannel(label="BB_signal")
    static public Dataset<Row> bbSignal;

    @InputChannel(label = "OptimalSignal")
    static public Dataset<Row> optimalSitgnal;

    @OutputChannel(label = "Signal")
    static public Dataset<Row> resultSignal;


    @Override
    public Option<String> run(SparkSession sparkSession, scala.collection.immutable.Map<String, Object> config) {
        

        Map<String, Object> configJava = JavaConverters.mapAsJavaMapConverter(config).asJava();

        JobProcessor jobProcessor = new JobProcessor(LinearSignalMLModel.class, sparkSession, configJava);

        jobProcessor.start();

        //step 1: merge all inputs datasets into Date, macd_signal, rsi_signal, bb_signal, optimal_signal
        Dataset<Row> source = LinearSignalMLModel.macdSignal.withColumnRenamed("Signal", "macd_signal")
            .join(LinearSignalMLModel.rsiSignal.withColumnRenamed("Signal", "rsi_signal"), "Date")
            .join(LinearSignalMLModel.bbSignal.withColumnRenamed("Signal", "bb_signal"), "Date")
            .join(LinearSignalMLModel.optimalSitgnal.withColumnRenamed("Signal", "optimal_signal"), "Date");


        //step 2: convert all the signals into indexes
        StringIndexerModel macdSignalIndexer = new StringIndexer()
            .setInputCol("macd_signal")
            .setOutputCol("macd_signal_index")
            .fit(source);
        Dataset<Row> indexedSource = macdSignalIndexer.transform(source);
        
        StringIndexerModel rsiSignalIndexer = new StringIndexer()
            .setInputCol("rsi_signal")
            .setOutputCol("rsi_signal_index")
            .fit(source);
        indexedSource = rsiSignalIndexer.transform(indexedSource);

        StringIndexerModel bbSignalIndexer = new StringIndexer()
            .setInputCol("bb_signal")
            .setOutputCol("bb_signal_index")
            .fit(source);
        indexedSource = bbSignalIndexer.transform(indexedSource);

        
        StringIndexerModel optimalSignalIndexer = new StringIndexer()
            .setInputCol("optimal_signal")
            .setOutputCol("label")
            .fit(source);
        indexedSource = optimalSignalIndexer.transform(indexedSource);


        //step 3: use verctor assembler for features (input data)

        VectorAssembler inputSignalsVectorAssembler = new VectorAssembler()
            .setInputCols(new String[]{"macd_signal_index", "rsi_signal_index", "bb_signal_index"})
            .setOutputCol("features");
        Dataset<Row> mlInput = inputSignalsVectorAssembler.transform(indexedSource).select("Date", "features", "label");


        //step 4: create linear model, fit data in it, then transform data into it
        LogisticRegression logisticRegression = new LogisticRegression()
            .setFeaturesCol("features")
            .setLabelCol("label")
            .setPredictionCol("prediction_signal_index");

        Dataset<Row> predictions = logisticRegression.fit(mlInput).transform(mlInput).select("Date", "prediction_signal_index");
        
        //step 5: convert predicted column from index into signal string
        IndexToString predictionIndexToSignal = new IndexToString()
            .setInputCol("prediction_signal_index")
            .setOutputCol("Signal")
            .setLabels(optimalSignalIndexer.labelsArray()[0]);

        Dataset<Row> result = predictionIndexToSignal.transform(predictions);

        //step 6: return dataset in format Date, Signal
        LinearSignalMLModel.resultSignal = result.select("Date", "Signal");


        jobProcessor.finish();

        return Some.apply("DONE");

    }
 
}
