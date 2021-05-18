package eu.koboo.kache.map;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheValidation<K, V> extends TimerTask {

    private static final ExecutorService VALIDATION_EXECUTOR = Executors.newFixedThreadPool(2);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(VALIDATION_EXECUTOR::shutdown));
    }

    private final CacheMap<K, V> cacheMap;

    public CacheValidation(CacheMap<K, V> cacheMap) {
        this.cacheMap = cacheMap;
    }

    @Override
    public void run() {
        VALIDATION_EXECUTOR.execute(() -> {
            for(K id : new ArrayList<>(cacheMap.getDataMap().keySet())) {
                V value = cacheMap.get(id);
                CacheData cacheData = cacheMap.getDataMap().get(id);
                if(value == null || cacheData == null) {
                    cacheMap.remove(id);
                    cacheMap.getDataMap().remove(id);
                    continue;
                }
                long validUntil = cacheData.getLastModified() + cacheMap.getTimeToLive();
                if(!cacheData.isForced() && validUntil < System.currentTimeMillis()) {
                    cacheMap.remove(id);
                    cacheMap.getDataMap().remove(id);
                }
            }
        });
    }
}
