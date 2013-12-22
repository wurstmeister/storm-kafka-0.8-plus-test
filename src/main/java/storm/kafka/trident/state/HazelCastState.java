package storm.kafka.trident.state;

import org.apache.commons.collections.MapUtils;
import storm.trident.state.TransactionalValue;
import storm.trident.state.map.IBackingMap;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HazelCastState<T> implements IBackingMap<TransactionalValue<Long>> {

    private HazelCastHandler handler;


    public HazelCastState(HazelCastHandler handler) {
        this.handler = handler;
    }

    public void addKeyValue(String key, Long value) {
        Map<String, Long> state = handler.getState();
        state.put(key, value);
    }

    @Override
    public String toString() {
        return handler.getState().toString();
    }


    @Override
    public void multiPut(List<List<Object>> keys, List<TransactionalValue<Long>> vals) {
        for (int i = 0; i < keys.size(); i++) {
            TridentTuple key = (TridentTuple) keys.get(i);
            Long value = vals.get(i).getVal();
            addKeyValue(key.getString(0), value);
            //System.out.println("[" + key.getString(0) + " - " + value + "]");
        }
    }


    public List multiGet(List<List<Object>> keys) {
        List<TransactionalValue<Long>> result = new ArrayList<TransactionalValue<Long>>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            TridentTuple key = (TridentTuple) keys.get(i);
            result.add(new TransactionalValue<Long>(0L, MapUtils.getLong(handler.getState(), key.getString(0), 0L)));
        }
        return result;
    }

}
