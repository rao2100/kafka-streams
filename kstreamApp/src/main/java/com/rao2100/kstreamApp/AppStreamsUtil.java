package com.rao2100.kstreamApp;

import java.util.Collections;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

import org.apache.avro.generic.GenericData;
import org.apache.avro.reflect.ReflectData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.avro.Schema;

public class AppStreamsUtil {

    private static Logger LOG = LoggerFactory.getLogger(AppStreamsUtil.class);

    public static Properties getStreamsConfiguration(final String bootstrapServers, final String schemaRegistryServers,
            final String appId) {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, appId);
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        streamsConfiguration.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryServers);
        // streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        // streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG,
        // "/tmp/kafka-streams/wordcount");
        // streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
        // "earliest");
        return streamsConfiguration;
    }

    public static GenericAvroSerde getGenericAvroServer(final String schemaRegistryServers, final Boolean isKeySerde) {
        GenericAvroSerde genericAvroSerde = new GenericAvroSerde();
        genericAvroSerde.configure(
                Collections.singletonMap(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryServers),
                isKeySerde);

        return genericAvroSerde;
    }


    public static GenericData.Record mapObjectToRecord(Object object) {
        Assert.notNull(object, "object must not be null");
        final Schema schema = ReflectData.get().getSchema(object.getClass());
        System.out.println(schema);
        final GenericData.Record record = new GenericData.Record(schema);
        schema.getFields().forEach(r -> record.put(r.name(), PropertyAccessorFactory.forDirectFieldAccess(object).getPropertyValue(r.name())));
        return record;
    }

    public static GenericData.Record mapObjectToRecord(Object object, Schema schema) {
        Assert.notNull(object, "object must not be null");
        final GenericData.Record record = new GenericData.Record(schema);
        schema.getFields().forEach(r -> record.put(r.name(), PropertyAccessorFactory.forDirectFieldAccess(object).getPropertyValue(r.name())));
        return record;
    }
    
}
