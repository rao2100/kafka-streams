#!/bin/bash
TOPIC=streams-hadoop-sink
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-console-consumer --from-beginning --bootstrap-server localhost:9092 \
        --property schema.registry.url="http://localhost:8081" \
        --topic $TOPIC --property print.key=true \
        --property print.timestamp=true
