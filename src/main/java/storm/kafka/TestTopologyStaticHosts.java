package storm.kafka;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import storm.kafka.trident.GlobalPartitionInformation;

public class TestTopologyStaticHosts {


    public static class PrinterBolt extends BaseBasicBolt {
        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
        }

        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            System.out.println(tuple.toString());
        }

    }

    public static void main(String[] args) throws Exception {

        GlobalPartitionInformation hostsAndPartitions = new GlobalPartitionInformation();
        hostsAndPartitions.addPartition(0, new Broker("localhost", 9092));
        BrokerHosts brokerHosts = new StaticHosts(hostsAndPartitions);

        SpoutConfig kafkaConfig = new SpoutConfig(brokerHosts, "storm-sentence", "", "storm");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("words", new KafkaSpout(kafkaConfig), 10);
        builder.setBolt("print", new PrinterBolt()).shuffleGrouping("words");
        LocalCluster cluster = new LocalCluster();
        Config config = new Config();
        cluster.submitTopology("kafka-test", config, builder.createTopology());

        Thread.sleep(600000);

    }
}
