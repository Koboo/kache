package eu.koboo.kache.cache.result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResolveManyResult<V> {

    private final CompletableFuture<Map<String, V>> future;

    public ResolveManyResult(CompletableFuture<Map<String, V>> future) {
        this.future = future;
    }

    public Map<String, V> sync() {
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CompletableFuture<Map<String, V>> future() {
        return future;
    }
}
