#!/bin/bash
APP_JAR=/home/ansible/Github/kafka-streams/kstreamApp/target/kstreamApp-0.0.1-SNAPSHOT.jar
CONFIG=/home/ansible/Github/kafka-streams/kstreamApp/src/main/resources/producer_simple.yml

java -jar $APP_JAR --spring.config.location=$CONFIG
