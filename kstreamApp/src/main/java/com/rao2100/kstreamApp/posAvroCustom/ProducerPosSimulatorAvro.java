package com.rao2100.kstreamApp.posAvroCustom;

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
import org.springframework.util.StreamUtils;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.rao2100.kstreamApp.AppConfig;
import com.rao2100.kstreamApp.AppStreamsUtil;
import com.rao2100.kstreamApp.pos.datagenerator.InvoiceGeneratorAvro;
import com.rao2100.kstreamApp.pos.types.LineItem;
import com.rao2100.kstreamApp.pos.types.PosInvoice;
import com.rao2100.kstreamApp.serde.JsonSerializer;

@Component
@ConditionalOnProperty(name = "usecase", havingValue = "producer_pos_avro")
public class ProducerPosSimulatorAvro implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(ProducerPosSimulatorAvro.class);

    @Autowired
    AppConfig appConfig;

    @Override
    public void run() {

        LOG.info("########################################");
        LOG.info("running ProducerPosSimulatorAvro");
        LOG.info("########################################");
        LOG.info("appConfig : {}".format(appConfig.toString()));


        LOG.info("start sending messages...");
        KafkaProducer<String, GenericRecord> producer = new KafkaProducer<>(getProps());

        // String schemaPath = "/home/ansible/Github/kafka-streams/kstreamApp/scripts/pos-avro/schema/avro/PosInvoiceAvro.avsc";
        String schemaPath = appConfig.getSchemas().get(0);
        String valueSchemaString = "";

        try {
            valueSchemaString = new String(Files.readAllBytes(Paths.get(schemaPath)));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Schema avroValueSchema = new Schema.Parser().parse(valueSchemaString);

        for (int i = 1; i <= appConfig.getProduceEventCount(); i++) {
            InvoiceGeneratorAvro invoiceGenerator = InvoiceGeneratorAvro.getInstance();
            GenericRecord valueRecord = invoiceGenerator.getNextInvoice(avroValueSchema);
            System.out.println(valueRecord);
            producer.send(new ProducerRecord<>(appConfig.getInputTopic(), valueRecord.get("storeID").toString(),
                    valueRecord));

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