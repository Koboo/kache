package eu.koboo.kache.cache.result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResolveResult<V> {

    private final String id;
    private final CompletableFuture<Map<String, V>> future;

    public ResolveResult(String id, CompletableFuture<Map<String, V>> future) {
        this.id = id;
        this.future = future;
    }

    public V sync() {
        try {
            Map<String, V> resolveMap = future.get();
            return resolveMap != null && !resolveMap.isEmpty() && resolveMap.containsKey(id) ? resolveMap.get(id) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CompletableFuture<V> future() {
        CompletableFuture<V> resolveFuture = new CompletableFuture<>();
        future.whenComplete((resolveMap, e) -> {
            if(resolveMap != null && !resolveMap.isEmpty() && resolveMap.containsKey(id)) {
                resolveFuture.complete(resolveMap.get(id));
            } else {
                resolveFuture.complete(null);
            }
        });
        return resolveFuture;
    }
}
