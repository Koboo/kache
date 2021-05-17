package eu.koboo.kache.map;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheValidation<ID, V> extends TimerTask {

    private static final ExecutorService VALIDATION_EXECUTOR = Executors.newFixedThreadPool(2);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(VALIDATION_EXECUTOR::shutdown));
    }

    private final CacheMap<ID, V> cacheMap;

    public CacheValidation(CacheMap<ID, V> cacheMap) {
        this.cacheMap = cacheMap;
    }

    @Override
    public void run() {
        VALIDATION_EXECUTOR.execute(() -> {
            for(ID id : new ArrayList<>(cacheMap.getDataMap().keySet())) {
                V value = cacheMap.get(id);
                CacheData cacheData = cacheMap.getDataMap().get(id);
                if(value == null || cacheData == null) {
                    cacheMap.remove(id);
                    cacheMap.getDataMap().remove(id);
                    continue;
                }
                long validUntil = cacheData.getLastModified() + cacheMap.getLifeTime();
                if(!cacheData.isForced() && validUntil < System.currentTimeMillis()) {
                    cacheMap.remove(id);
                    cacheMap.getDataMap().remove(id);
                }
            }
        });
    }
}
