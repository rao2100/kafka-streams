#!/bin/bash
TOPIC=streams-movies-input
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-avro-console-consumer --from-beginning --bootstrap-server localhost:9092 \
        --property schema.registry.url="http://localhost:8081" \
        --topic $TOPIC --property print.key=true \
        --key-deserializer=org.apache.kafka.common.serialization.StringDeserializer \
        --property print.timestamp=true
