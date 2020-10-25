package com.rao2100.kstreamApp;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

public class StreamsUtil {

    private static Logger LOG = LoggerFactory.getLogger(ProducerSimple.class);

    public static Properties getStreamsConfiguration(final String bootstrapServers, final String appId) {
        final Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG,  appId);
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG,  appId);
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        // streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        // streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams/wordcount");
        return streamsConfiguration;
    }
}

    