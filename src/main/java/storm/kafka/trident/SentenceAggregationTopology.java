package storm.kafka.trident;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.StormSubmitter;
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

public class SentenceAggregationTopology {

	public static void main(String[] args) throws Exception {

		BrokerHosts brokerHosts = new ZkHosts("localhost");

		TridentKafkaConfig kafkaConfig = new TridentKafkaConfig(brokerHosts, "storm-sentence", "storm");
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		TransactionalTridentKafkaSpout kafkaSpout = new TransactionalTridentKafkaSpout(kafkaConfig);

		TridentTopology topology = new TridentTopology();

		TridentState wordCounts = topology.newStream("kafka", kafkaSpout).shuffle().
				each(new Fields("str"), new WordSplit(), new Fields("word")).
				groupBy(new Fields("word")).
				persistentAggregate(new HazelCastStateFactory(), new Count(), new Fields("aggregates_words")).parallelismHint(2);

		LocalDRPC drpc = new LocalDRPC();
		topology.newDRPCStream("words", drpc)
				.each(new Fields("args"), new Split(), new Fields("word"))
				.groupBy(new Fields("word"))
				.stateQuery(wordCounts, new Fields("word"), new MapGet(), new Fields("count"))
				.each(new Fields("count"), new FilterNull())
				.aggregate(new Fields("count"), new Sum(), new Fields("sum"));

		Config config = new Config();
		config.put(Config.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS, 2000);

		if (args != null && args.length > 0) {
			config.setNumWorkers(2);
			config.setMaxTaskParallelism(5);
			StormSubmitter.submitTopology(args[0], config,
					topology.build());
		} else {
			config.setNumWorkers(2);
			config.setMaxTaskParallelism(2);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("kafka", config, topology.build());
			while (true) {
				System.out.println("Word count: " + drpc.execute("words", "the"));
				Utils.sleep(1000);
			}

		}
	}
}
