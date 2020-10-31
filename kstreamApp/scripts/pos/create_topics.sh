#!/bin/bash
TOPIC=streams-pos-input
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-topics --create --bootstrap-server localhost:9092 \
        --replication-factor 1 --partitions 1 \
        --topic $TOPIC 

TOPIC=streams-shipment
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-topics --create --bootstrap-server localhost:9092 \
        --replication-factor 1 --partitions 1 \
        --topic $TOPIC 

TOPIC=streams-loyalty
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-topics --create --bootstrap-server localhost:9092 \
        --replication-factor 1 --partitions 1 \
        --topic $TOPIC 

TOPIC=streams-hadoop-sink
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-topics --create --bootstrap-server localhost:9092 \
        --replication-factor 1 --partitions 1 \
        --topic $TOPIC 

