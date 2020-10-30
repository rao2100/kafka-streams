## Wordcount
### Features
* DSL - foreach, flatMapValues, groupBy, count
 
### To run
```bash
mvn clean install
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-plaintext-input
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-wordcount-output
./scripts/wordcount/run_wordcount.sh
./scripts/wordcount/consumer_wc_in.sh
./scripts/wordcount/consumer_wc_out.sh
```

## Movies
### Features
* AvroProducer, GenericRecord 
* DSL - map, filter, groupByKey, reduce

### To run
```bash
mvn clean install
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-movies-input
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-movies-intermediate
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-movies-output


./scripts/movies/run_movies.sh
./scripts/movies/consumer_movie_in.sh
./scripts/movies/consumer_movie_int.sh
./scripts/movies/consumer_movie_out.sh
```


# Links:
[Visualize topology](https://zz85.github.io/kafka-streams-viz/)