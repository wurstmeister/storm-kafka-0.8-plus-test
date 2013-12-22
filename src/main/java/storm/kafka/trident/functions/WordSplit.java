package storm.kafka.trident.functions;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class WordSplit extends BaseFunction {
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String sentence = (String) tuple.getValue(0);
        if (sentence != null) {
            sentence = sentence.replaceAll("\r", "");
            sentence = sentence.replaceAll("\n", "");
            for (String word : sentence.split(" ")) {
                collector.emit(new Values(word));
            }
        }
    }
}