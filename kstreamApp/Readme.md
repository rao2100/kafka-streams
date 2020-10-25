```bash
mvn clean install
## wordcount
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-plaintext-input
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-wordcount-output
./scripts/wordcount/run_wordcount.sh
./scripts/wordcount/consumer_wc_out.sh

## windowed

```