package com.rao2100.kstreamApp.movies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.rao2100.kstreamApp.AppConfig;

@Component
@ConditionalOnProperty(name = "usecase", havingValue = "producer_movies")
public class ProducerMovies implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(ProducerMovies.class);

    @Autowired
    AppConfig appConfig;

    @Override
    public void run() {

        LOG.info("########################################");
        LOG.info("running ProducerMovies");
        LOG.info("########################################");
        LOG.info("appConfig : {}".format(appConfig.toString()));

        KafkaProducer producer = new KafkaProducer<String, GenericRecord>(getProps());

        LOG.info("start sending messages...");

        // for (int i = 0; i < appConfig.getProduceEventCount(); i++) {
        // producer.send(new ProducerRecord<>(appConfig.getInputTopic(), i, "all streams
        // lead to kafka"));
        // }

        String schemaPath = "/home/ansible/Github/kafka-streams/kstreamApp/scripts/movies/movies.avsc";
        String valueSchemaString = "";

        try {
            valueSchemaString = new String(Files.readAllBytes(Paths.get(schemaPath)));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Schema avroValueSchema = new Schema.Parser().parse(valueSchemaString);

        ArrayList<String> movieList = new ArrayList<>();
        movieList.add("Die Hard");
        movieList.add("X-Men");
        movieList.add("Star Wars");
        movieList.add("5th Element");
        movieList.add("Aliens");
        movieList.add("Predator");
        movieList.add("Tenent");
        movieList.add("Jumanji");
        movieList.add("Dune");
        movieList.add("Star Trek");

        for (int i = 1; i <= appConfig.getProduceEventCount(); i++) {
            GenericRecord valueRecord = new GenericData.Record(avroValueSchema);
            valueRecord.put("title", movieList.get(i-1));
            valueRecord.put("sale_ts", "2019-07-18T10:00:00Z");
            valueRecord.put("ticket_total_value", 10 * i);
            LOG.info(valueRecord.toString());
            producer.send(new ProducerRecord<>(appConfig.getInputTopic(), "key-" + i, valueRecord));
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        LOG.info("finished sending messages...");

    }

    private Properties getProps() {

        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, appConfig.getAppId());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, appConfig.getSchemaRegistryUrl());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 5);

        return props;
    }

}