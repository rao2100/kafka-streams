package com.rao2100.kstreamApp.wordcount;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.rao2100.kstreamApp.AppConfig;

@Component
@ConditionalOnProperty(name = "usecase", havingValue = "producer_wordcount")
public class ProducerWordcount implements Runnable{

    private static Logger LOG = LoggerFactory.getLogger(ProducerWordcount.class);

    @Autowired
    AppConfig appConfig;

    @Override
    public void run() {
        
        LOG.info("########################################");
        LOG.info("running ProducerWordcount");
        LOG.info("########################################");
        LOG.info("appConfig : {}".format(appConfig.toString()));
               
        KafkaProducer producer = new KafkaProducer<Integer, String>(getProps());

        LOG.info("start sending messages...");
        for (int i = 0; i < appConfig.getProduceEventCount(); i++) {
            producer.send(new ProducerRecord<>(appConfig.getInputTopic(), i, "all streams lead to kafka"));
        }

        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        LOG.info("finished sending messages...");

    }

    private Properties getProps(){

        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, appConfig.getAppId());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,5);
        

        return props;


    }
    
}