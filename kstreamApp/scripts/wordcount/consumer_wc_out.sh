#!/bin/bash
TOPIC=streams-wordcount-output
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-console-consumer --from-beginning --bootstrap-server localhost:9092 --topic $TOPIC \
        --property print.key=true \
        --key-deserializer=org.apache.kafka.common.serialization.StringDeserializer \
        --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer \
        --property print.timestamp=true \
         --formatter kafka.tools.DefaultMessageFormatter