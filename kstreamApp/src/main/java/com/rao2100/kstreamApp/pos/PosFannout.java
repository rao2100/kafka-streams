package com.rao2100.kstreamApp.pos;

import java.util.List;
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
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;

import org.apache.avro.generic.GenericRecord;

import com.rao2100.kstreamApp.AppConfig;
import com.rao2100.kstreamApp.AppStreamsUtil;
import com.rao2100.kstreamApp.pos.AppSerdes;
import com.rao2100.kstreamApp.pos.types.PosInvoice;

@Component
@ConditionalOnProperty(name = "usecase", havingValue = "pos-fannout")
public class PosFannout implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(PosFannout.class);

    @Autowired
    AppConfig appConfig;

    public void runWordCount() {

        LOG.info("########################################");
        LOG.info("running PosFannout");
        LOG.info("########################################");

        Properties streamsConfiguration = this.getStreamsConfig();
        final Topology topology = createStreamTopology(appConfig.getInputTopic(), appConfig.getOutputTopics());
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

    private Topology createStreamTopology(final String inputTopic, final List<String> outputTopics) {

        String shipmentTopicName = appConfig.getOutputTopics().get(0);
        String notificationTopic = appConfig.getOutputTopics().get(1);
        String hadoopTopic = appConfig.getOutputTopics().get(2);

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, PosInvoice> KS0 = builder.stream(inputTopic,
            Consumed.with(AppSerdes.String(), AppSerdes.PosInvoice()));

        KS0.filter((k, v) ->
            v.getDeliveryType().equalsIgnoreCase(PosConfig.DELIVERY_TYPE_HOME_DELIVERY))
            .to(shipmentTopicName, Produced.with(AppSerdes.String(), AppSerdes.PosInvoice()));

        KS0.filter((k, v) ->
            v.getCustomerType().equalsIgnoreCase(PosConfig.CUSTOMER_TYPE_PRIME))
            .mapValues(invoice -> RecordBuilder.getNotification(invoice))
            .to(notificationTopic, Produced.with(AppSerdes.String(), AppSerdes.Notification()));

        KS0.mapValues(invoice -> RecordBuilder.getMaskedInvoice(invoice))
            .flatMapValues(invoice -> RecordBuilder.getHadoopRecords(invoice))
            .to(hadoopTopic, Produced.with(AppSerdes.String(), AppSerdes.HadoopRecord()));

        return builder.build();
    }

    private Properties getStreamsConfig() {
        Properties streamsConfiguration = AppStreamsUtil.getStreamsConfiguration(appConfig.getBootstrapServers(),
                appConfig.getSchemaRegistryUrl(), appConfig.getAppId());        

        return streamsConfiguration;
    }

    @Override
    public void run() {

        runWordCount();
    }

}