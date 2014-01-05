package storm.kafka.tools;

import backtype.storm.generated.DRPCExecutionException;
import backtype.storm.utils.DRPCClient;
import backtype.storm.utils.Utils;
import org.apache.thrift7.TException;

public class DrpcClient {
    public static void main(String[] args) throws TException, DRPCExecutionException {
        DRPCClient client = new DRPCClient(args[0], Integer.parseInt(args[1]));
        while (true) {
            System.out.println("Word count: " + client.execute("words", "cow"));
            Utils.sleep(1000);
        }
    }
}
