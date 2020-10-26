# #!/bin/bash
# reset application
APP_ID=movies-track
INPUT_TOPICS="streams-movies-input"
kafka_path=/home/ansible/programs/confluent-5.5.1/bin/
sudo $kafka_path/kafka-streams-application-reset --bootstrap-servers localhost:9092 --application-id $APP_ID --input-topics $INPUT_TOPICS
rm -rf /tmp/kafka-streams/
# produce records
APP_JAR=/home/ansible/Github/kafka-streams/kstreamApp/target/kstreamApp-0.0.1-SNAPSHOT.jar
CONFIG=/home/ansible/Github/kafka-streams/kstreamApp/scripts/movies/producer_movies.yml
java -jar $APP_JAR --spring.config.location=$CONFIG

sleep 10

# run movies-track
APP_JAR=/home/ansible/Github/kafka-streams/kstreamApp/target/kstreamApp-0.0.1-SNAPSHOT.jar
CONFIG=/home/ansible/Github/kafka-streams/kstreamApp/scripts/movies/application_movies.yml
java -jar $APP_JAR --spring.config.location=$CONFIG