package storm.kafka.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import storm.kafka.BrokerHosts;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import storm.kafka.trident.functions.WordSplit;
import storm.kafka.trident.state.HazelCastStateFactory;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.operation.builtin.FilterNull;
import storm.trident.operation.builtin.MapGet;
import storm.trident.operation.builtin.Sum;
import storm.trident.testing.Split;

import java.util.Arrays;

public class SentenceAggregationTopology {

    private final BrokerHosts brokerHosts;

    public SentenceAggregationTopology(String kafkaZookeeper) {
        brokerHosts = new ZkHosts(kafkaZookeeper);
    }

    public StormTopology buildTopology() {
        return buildTopology(null);
    }

    public StormTopology buildTopology(LocalDRPC drpc) {
        TridentKafkaConfig kafkaConfig = new TridentKafkaConfig(brokerHosts, "storm-sentence", "storm");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        TransactionalTridentKafkaSpout kafkaSpout = new TransactionalTridentKafkaSpout(kafkaConfig);
        TridentTopology topology = new TridentTopology();

        TridentState wordCounts = topology.newStream("kafka", kafkaSpout).shuffle().
                each(new Fields("str"), new WordSplit(), new Fields("word")).
                groupBy(new Fields("word")).
                persistentAggregate(new HazelCastStateFactory(), new Count(), new Fields("aggregates_words")).parallelismHint(2);


        topology.newDRPCStream("words", drpc)
                .each(new Fields("args"), new Split(), new Fields("word"))
                .groupBy(new Fields("word"))
                .stateQuery(wordCounts, new Fields("word"), new MapGet(), new Fields("count"))
                .each(new Fields("count"), new FilterNull())
                .aggregate(new Fields("count"), new Sum(), new Fields("sum"));

        return topology.build();
    }

    public static void main(String[] args) throws Exception {

        String kafkaZk = args[0];
        SentenceAggregationTopology sentenceAggregationTopology = new SentenceAggregationTopology(kafkaZk);
        Config config = new Config();
        config.put(Config.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS, 2000);

        if (args != null && args.length > 1) {
            String name = args[1];
            String dockerIp = args[2];
            config.setNumWorkers(2);
            config.setMaxTaskParallelism(5);
            config.put(Config.NIMBUS_HOST, dockerIp);
            config.put(Config.NIMBUS_THRIFT_PORT, 6627);
            config.put(Config.STORM_ZOOKEEPER_PORT, 2181);
            config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(dockerIp));
            StormSubmitter.submitTopology(name, config, sentenceAggregationTopology.buildTopology());
        } else {
            LocalDRPC drpc = new LocalDRPC();
            config.setNumWorkers(2);
            config.setMaxTaskParallelism(2);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("kafka", config, sentenceAggregationTopology.buildTopology(drpc));
            while (true) {
                System.out.println("Word count: " + drpc.execute("words", "the"));
                Utils.sleep(1000);
            }

        }
    }
}
