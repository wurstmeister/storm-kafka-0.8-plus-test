package storm.kafka.tools;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

public class StormProducer {


    private static String[] sentences = new String[]{
            "the cow jumped over the moon",
            "the man went to the store and bought some candy",
            "four score and seven years ago",
            "how many apples can you eat",
    };

    public static void main(String args[]) throws InterruptedException {
        Properties props = new Properties();

        props.put("metadata.broker.list", args[0]);
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        while (true) {
            for (String sentence : sentences) {
                KeyedMessage<String, String> data = new KeyedMessage<String, String>("storm-sentence", sentence);
                producer.send(data);
                Thread.sleep(10);
            }
        }

    }
}
