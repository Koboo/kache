package eu.koboo.netsync.operation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncModel extends ConcurrentHashMap<String, Object> {

    private long lastTimeModified;

    public SyncModel() {
        this.lastTimeModified = System.currentTimeMillis();
    }

    public long getLastTimeModified() {
        return lastTimeModified;
    }

    public void setLastTimeModified(long lastTimeModified) {
        this.lastTimeModified = lastTimeModified;
    }

    @Override
    public Object put(String key, Object value) {
        setLastTimeModified(System.currentTimeMillis());
        return super.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        setLastTimeModified(System.currentTimeMillis());
        return super.remove(key, value);
    }

    public <T> T get(String key) {
        return (T) super.get(key);
    }

    public <T> T getOrDefault(String key, T defaultT) {
        return (T) super.getOrDefault(key, defaultT);
    }

    public boolean containsKeys(String... keys) {
        for(String key : keys) {
            if(containsKey(key))
                return true;
        }
        return false;
    }

    public boolean containsValues(Object... objects) {
        for(Object object : objects) {
            if(containsValue(object))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SyncModel{")
        .append("Map{");
        for(Map.Entry<String, Object> entry : entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue().toString()).append(",");
        }
        builder.append("},lastTimeModified=").append(lastTimeModified).append("}");
        return builder.toString();
    }
}
