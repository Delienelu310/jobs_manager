package com.ilumusecase.scripts;

import java.util.Random;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public final class App {

    public static void main(String[] args) {
        Properties props = new Properties();
		props.put("bootstrap.servers", "ilum-kafka:9092");
		props.put("acks", "all"); 
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Random random = new Random();

        try(Producer<String, String> producer = new KafkaProducer<>(props);){
            while (true)
            {
                Thread.sleep(500);

                producer.send(new ProducerRecord<String, String>("internal_667868a20e0aa55dd91b5b77", 
                    Integer.toString(random.nextInt(10) + 1) + "," + Long.toString(random.nextLong())
                ));
            }
        }catch(Exception exception){

        }
		
    }
}
