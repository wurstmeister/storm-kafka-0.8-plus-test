storm-kafka-0.8-plus-test
=========================

Test project for storm-kafka-0.8-plus based on information provided in:

- [https://github.com/nathanmarz/storm-contrib/blob/master/storm-kafka/src/jvm/storm/kafka/TestTopology.java] (https://github.com/nathanmarz/storm-contrib/blob/master/storm-kafka/src/jvm/storm/kafka/TestTopology.java)
- [https://github.com/nathanmarz/storm/wiki/Trident-tutorial] (https://github.com/nathanmarz/storm/wiki/Trident-tutorial)
- [https://github.com/nathanmarz/storm/wiki/Trident-state] (https://github.com/nathanmarz/storm/wiki/Trident-state)
- [https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example] (https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example)

Also contains an attempt at a sample implementation of trident state based on [Hazelcast] (http://www.hazelcast.com/)


##Environment setup with [Docker](https://www.docker.io/)

If you are using a Mac follow the instructions [here](http://docs.docker.io/en/latest/installation/vagrant/) to setup a docker environment.
Make sure you forward all ports from your vagrant environment: ```$ FORWARD_DOCKER_PORTS='true' vagrant up```

- Start a broker
    - ```start-broker.sh <brokerId> <port> <hostIp>```
- Start a kafka shell
    - ```start-kafka-shell.sh```
- From within the shell, create a topic
    - ```$KAFKA_HOME/bin/kafka-topics.sh --create --topic storm-sentence --partitions 2 --zookeeper $ZK_PORT_2181_TCP_ADDR --replication-factor 1```
- Start a Storm cluster
    - ```start-storm.sh```
- For more details and troubleshooting see [https://github.com/wurstmeister/kafka-docker](https://github.com/wurstmeister/kafka-docker) and [https://github.com/wurstmeister/storm-docker](https://github.com/wurstmeister/storm-docker)


##Build for running locally:

- ```mvn clean package```

##Build for running on a Storm cluster:

- ```mvn clean package -P cluster```

##Running the test topologies locally

- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.trident.SentenceAggregationTopology <kafkaZookeeper>```
- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.KafkaSpoutTestTopology <kafkaZookeeper>```
- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.TestTopologyStaticHosts```

##Running the test topologies on a storm cluster


- ```storm jar target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.trident.SentenceAggregationTopology <kafkaZookeeper> sentences <dockerIp>```
- ```storm jar target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.KafkaSpoutTestTopology <kafkaZookeeper> sentences <dockerIp>```

The Storm UI will be available under: ```http://<dockerIp>:49080/```

The Logviewer will be available under: ```http://<dockerIp>:49000/``` e.g. ```http://<dockerIp>:49000/log?file=supervisor.log```

##Producing data

To feed the topologies with data, start the StormProducer (built in local mode)

- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.tools.StormProducer <dockerIp>:<kafkaPort>```

Alternatively use the kafka console producer from within the kafka shell (see above)

- ```$KAFKA_HOME/bin/kafka-console-producer.sh --topic=storm-sentence --broker-list=<dockerIp>:<kafkaPort>```

##Consuming data

To run a DRPC query, start the DrpcClient (built in local mode)

- ```java -cp target/storm-kafka-0.8-plus-test-0.1.0-SNAPSHOT-jar-with-dependencies.jar storm.kafka.tools.DrpcClient <dockerIp> 49772```
