package eu.koboo.kache.cache.future;

import eu.koboo.kache.cache.Cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface FutureCache<V extends Serializable> extends Cache<V> {

    CompletableFuture<Map<String, Boolean>> existsMany(List<String> id);

    CompletableFuture<Boolean> exists(String id);

    CompletableFuture<Map<String, V>> resolveMany(List<String> listToResolve);

    CompletableFuture<V> resolve(String id);

    CompletableFuture<Map<String, V>> resolveAll();

}
