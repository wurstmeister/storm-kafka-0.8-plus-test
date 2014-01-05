storm-kafka-0.8-plus-test
=========================

Simple test project for storm-kafka-0.8-plus based on information provided in:

- [https://github.com/nathanmarz/storm-contrib/blob/master/storm-kafka/src/jvm/storm/kafka/TestTopology.java] (https://github.com/nathanmarz/storm-contrib/blob/master/storm-kafka/src/jvm/storm/kafka/TestTopology.java)
- [https://github.com/nathanmarz/storm/wiki/Trident-tutorial] (https://github.com/nathanmarz/storm/wiki/Trident-tutorial)
- [https://github.com/nathanmarz/storm/wiki/Trident-state] (https://github.com/nathanmarz/storm/wiki/Trident-state)
- [https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example] (https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example)

Also contains an attempt at a sample implementation of trident state based on [Hazelcast] (http://www.hazelcast.com/)


##Build for running locally:

```mvn clean package```

##Build for running on a Storm cluster:

```mvn clean package -P cluster```

##Prepare Kafka

The tests are using a topic called "storm-sentence", it can be created using:

```bin/kafka-create-topic.sh --topic storm-sentence --partition 2 --replica 1 --zookeeper localhost```

##Running the test topologies locally

There are 3 test topologies:

- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.trident.SentenceAggregationTopology <kafkaZookeeper>```
- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.KafkaSpoutTestTopology <kafkaZookeeper>```
- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.TestTopologyStaticHosts```

##Running the test topologies on a storm cluster

If you are using a Mac follow the instructions here: [http://docs.docker.io/en/latest/installation/vagrant/](http://docs.docker.io/en/latest/installation/vagrant/) to setup a docker environment.
Make sure you forward all ports from your vagrant environment: ```$ FORWARD_DOCKER_PORTS='true' vagrant up```

- ```start-storm.sh```
- ```storm jar target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.trident.SentenceAggregationTopology <kafkaZookeeper> sentences <dockerIp>```

The Storm UI will be available under: ```http://<dockerIp>:49080/```
The Logviewer will be available under: ```http://<dockerIp>:49000/``` e.g. ```http://<dockerIp>:49000/log?file=supervisor.log```

##Producing data

To feed the topologies with data, start the StormProducer (built in local mode)

- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.tools.StormProducer <kafkaIp>:9092```

##Consuming data

To run a DRPC query, start the DrpcClient (built in local mode)

- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.tools.DrpcClient <dockerIp> 49772```
