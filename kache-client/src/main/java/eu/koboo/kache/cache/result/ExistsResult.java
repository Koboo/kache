package eu.koboo.kache.cache.result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ExistsResult {

    private final String id;
    private final CompletableFuture<Map<String, Boolean>> future;

    public ExistsResult(String id, CompletableFuture<Map<String, Boolean>> future) {
        this.id = id;
        this.future = future;
    }

    public boolean sync() {
        try {
            Map<String, Boolean> existsMap = future.get();
            return existsMap != null && !existsMap.isEmpty() && existsMap.containsKey(id) && existsMap.get(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public CompletableFuture<Boolean> future() {
        CompletableFuture<Boolean> existsFuture = new CompletableFuture<>();
        future.whenComplete((map, e) -> existsFuture.complete(map != null && !map.isEmpty() && map.containsKey(id) && map.get(id)));
        return existsFuture;
    }
}
