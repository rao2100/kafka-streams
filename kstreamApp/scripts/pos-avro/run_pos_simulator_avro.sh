# #!/bin/bash
# produce records
APP_JAR=/home/ansible/Github/kafka-streams/kstreamApp/target/kstreamApp-0.0.1-SNAPSHOT.jar
CONFIG=/home/ansible/Github/kafka-streams/kstreamApp/scripts/pos-avro/producer_pos_avro.yml
java -jar $APP_JAR --spring.config.location=$CONFIG
