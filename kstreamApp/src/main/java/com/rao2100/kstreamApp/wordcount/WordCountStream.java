package com.rao2100.kstreamApp.wordcount;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;


import com.rao2100.kstreamApp.AppConfig;
import com.rao2100.kstreamApp.StreamsUtil;

@Component
@ConditionalOnProperty(name = "usecase", havingValue = "wordcount", matchIfMissing = true)
public class WordCountStream implements Runnable{

    private static Logger LOG = LoggerFactory.getLogger(WordCountStream.class);

    @Autowired
    AppConfig appConfig;


    final Serde<String> stringSerde = Serdes.String();
    final Serde<Long> longSerde = Serdes.Long();


    public void runWordCount() {

        LOG.info("########################################");
        LOG.info("running WordCountStream");
        LOG.info("########################################");
 
        Properties streamsConfiguration = StreamsUtil.getStreamsConfiguration(appConfig.getBootstrapServers(), appConfig.getSchemaRegistryUrl(), appConfig.getAppId());
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        final StreamsBuilder builder = new StreamsBuilder();
        Topology topology =  createWordCountTopology(builder, appConfig.getInputTopic(), appConfig.getOutputTopic());
        final KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);

        streams.cleanUp();

        LOG.info(topology.describe().toString());

        LOG.info("Starting stream");
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Shutting down stream");
            streams.close();
        }));
    }


    private Topology createWordCountTopology(final StreamsBuilder builder, final String inputTopic, final String outputTopic) {
  
        final KStream<String, String> textLines = builder.stream(inputTopic, Consumed.with(stringSerde, stringSerde));
        // print line for each record
        textLines.foreach((k,v) -> System.out.println("key: " + k + " value: " + v));

        // do wordcount
        final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
        final KTable<String, Long> wordCounts = textLines
                .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
                .groupBy((keyIgnored, word) -> word)
                .count();

        wordCounts.toStream().to(outputTopic, Produced.with(stringSerde, longSerde));

        return builder.build();
    }

    @Override
    public void run() {
     
        runWordCount();
    }

}