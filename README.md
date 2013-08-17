storm-kafka-0.8-plus-test
=========================

Simple test project for storm-kafka-0.8-plus based on information provided in:

- [https://github.com/nathanmarz/storm-contrib/blob/master/storm-kafka/src/jvm/storm/kafka/TestTopology.java] (https://github.com/nathanmarz/storm-contrib/blob/master/storm-kafka/src/jvm/storm/kafka/TestTopology.java)
- [https://github.com/nathanmarz/storm/wiki/Trident-tutorial] (https://github.com/nathanmarz/storm/wiki/Trident-tutorial)
- [https://github.com/nathanmarz/storm/wiki/Trident-state] (https://github.com/nathanmarz/storm/wiki/Trident-state)
- [https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example] (https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example)

Also contains a sample implementation of trident state based on [Hazelcast] (http://www.hazelcast.com/)


##Build for running locally:

```mvn clean package -P local```

##Build for running on a Strom cluster:

```mvn clean package -P cluster```

##Running the test topologies locally

The tests are using a topic called "storm-sentence", it can be created using:

```bin/kafka-create-topic.sh --topic storm-sentence --partition 2 --replica 1 --zookeeper localhost```

There are 3 test topologies:

- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.trident.SentenceAggregationTopology```
- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.trident.TestTopology```
- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.trident.TestTopologyStaticHosts```

To feed the topologies with data, start the StormProducer

- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.trident.StormProducer```