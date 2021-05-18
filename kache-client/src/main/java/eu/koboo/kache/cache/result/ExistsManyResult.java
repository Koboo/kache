package eu.koboo.kache.cache.result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ExistsManyResult {

    private final CompletableFuture<Map<String, Boolean>> future;

    public ExistsManyResult(CompletableFuture<Map<String, Boolean>> future) {
        this.future = future;
    }

    public Map<String, Boolean> sync() {
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public CompletableFuture<Map<String, Boolean>> future() {
        return future;
    }
}
