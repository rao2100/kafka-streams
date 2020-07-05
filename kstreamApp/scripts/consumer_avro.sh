#!/bin/bash
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-avro-console-consumer --from-beginning --bootstrap-server 10.0.150.172:9092 \
        --property schema.registry.url="http://10.0.150.172:8081" \
        --topic t_input_data --property print.key=true \
        --key-deserializer=org.apache.kafka.common.serialization.StringDeserializer \
        --property print.timestamp=true
