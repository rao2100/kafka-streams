#!/bin/bash
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-console-consumer --from-beginning --bootstrap-server 10.0.150.172:9092 --topic input-producer \
        --property print.key=true \
        --key-deserializer=org.apache.kafka.common.serialization.IntegerDeserializer \
        --property print.timestamp=true