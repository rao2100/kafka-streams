package com.rao2100.kstreamApp.movies;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Grouped;

import org.apache.avro.generic.GenericRecord;

import com.rao2100.kstreamApp.AppConfig;
import com.rao2100.kstreamApp.StreamsUtil;

@Component
@ConditionalOnProperty(name = "usecase", havingValue = "movies-track")
public class MoviesTracker implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(MoviesTracker.class);

    @Autowired
    AppConfig appConfig;

    public void runWordCount() {

        LOG.info("########################################");
        LOG.info("running MoviesTracker");
        LOG.info("########################################");

        Properties streamsConfiguration = this.getStreamsConfig();

        final StreamsBuilder builder = this.createStreamTopology(appConfig.getInputTopic(), appConfig.getOutputTopic());
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private StreamsBuilder createStreamTopology(final String inputTopic, final String outputTopic) {

        StreamsBuilder builder = new StreamsBuilder();

        GenericAvroSerde genericAvroSerde = StreamsUtil.getGenericAvroServer(appConfig.getSchemaRegistryUrl(), false);
        final KStream<String, GenericRecord> moviesSales = builder.stream(inputTopic);

        KStream<String, GenericRecord> filteredSales = moviesSales
                .filter((k, v) -> 50 < (int) v.get("ticket_total_value"));

        KStream<String, GenericRecord> rekeyedSales = filteredSales
                .map((k, v) -> new KeyValue<>(v.get("title").toString(), v));
        rekeyedSales.to(appConfig.getIntermediateTopic(), Produced.with(Serdes.String(), genericAvroSerde));

        KTable<String, Integer> aggregateSales = rekeyedSales
                .map((k, v) -> new KeyValue<>(k, (int) v.get("ticket_total_value")))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Integer())).reduce(Integer::sum);

        aggregateSales.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Integer()));

        return builder;
    }

    private Properties getStreamsConfig() {
        Properties streamsConfiguration = StreamsUtil.getStreamsConfiguration(appConfig.getBootstrapServers(),
                appConfig.getSchemaRegistryUrl(), appConfig.getAppId());
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);

        return streamsConfiguration;
    }

    @Override
    public void run() {

        runWordCount();
    }

}