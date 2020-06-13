kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-plaintext-input
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-wordcount-output
echo -e "all streams lead to kafka\nhello kafka streams\njoin kafka summit" > /tmp/file-input.txt
cat /tmp/file-input.txt | kafka-console-producer --broker-list localhost:9092 --topic streams-plaintext-input

kafka-console-consumer --bootstrap-server localhost:9092 --topic streams-plaintext-input --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.key=true 

kafka-console-consumer --bootstrap-server localhost:9092 --topic streams-wordcount-output --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer