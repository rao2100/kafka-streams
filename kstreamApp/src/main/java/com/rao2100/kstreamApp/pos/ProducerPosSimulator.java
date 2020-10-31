package com.rao2100.kstreamApp.pos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import com.rao2100.kstreamApp.pos.types.PosInvoice;
import com.rao2100.kstreamApp.serde.JsonSerializer;

@Component
@ConditionalOnProperty(name = "usecase", havingValue = "producer_pos")
public class ProducerPosSimulator implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(ProducerPosSimulator.class);

    @Autowired
    AppConfig appConfig;

    @Override
    public void run() {

        LOG.info("########################################");
        LOG.info("running ProducerPosSimulator");
        LOG.info("########################################");
        LOG.info("appConfig : {}".format(appConfig.toString()));

        
        int noOfProducers = 1;
        int produceSpeed = 1000; // sleep in ms


        LOG.info("start sending messages...");
        KafkaProducer<String, PosInvoice> producer = new KafkaProducer<>(getProps());
        
        ExecutorService executor = Executors.newFixedThreadPool(noOfProducers);
        final List<RunnableProducer> runnableProducers = new ArrayList<>();

        for (int i = 0; i < noOfProducers; i++) {
            RunnableProducer runnableProducer = new RunnableProducer(i, producer, appConfig.getInputTopic(), produceSpeed);
            runnableProducers.add(runnableProducer);
            executor.submit(runnableProducer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (RunnableProducer p : runnableProducers) p.shutdown();
            executor.shutdown();
            LOG.info("Closing Executor Service");
            try {
                executor.awaitTermination(produceSpeed * 2, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

        LOG.info("finished sending messages...");

    }

    private Properties getProps() {

        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, appConfig.getAppId());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);        
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 5);

        return props;
    }

}