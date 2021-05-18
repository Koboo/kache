# Koboo's Cache (Kache)

Kache stands for Koboo's Cache and is a minimalistic and lightweight framework 
for remote and local caches. The network transport was implemented with 
Endpoint-Netty and is based on client and server. The server caches the data regularly in a 
ConcurrentHashMap and the client has the possibility to access it via the LocalCache object.

Note: The transport encoding is based on the Protocol.NATIVE of Endpoint-Netty, 
but the object serialization is based on the framework fast-serialization, 
whereby objects in the cache have to extend the class Serializable!

# Important

It's recommend to use this framework only on localhost-running apps to share maps across JVM.

# Usage
Create a new ``KacheClient``:
````java
public class SomeClass {
    
    public static void main(String[] args) {
        // To use Unix-Domain-Sockets (all apps have to run on localhost)
        KacheClient client = new KacheClient();
        
        // To use the normal TCP-transport (apps can run on different machines)
        String host = "127.0.0.1";
        int port = 6565;
        KacheClient client = new KacheClient(host, port);
    }
    
}
````

````java
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SomeClass {

    public static void main(String[] args) {
        KacheClient client = new KacheClient();

        String key = "key"; // any key
        TestObj object = new TestObj("", 1L, -1); // any object, have to extend Serializable 

        SharedCache<Object> cache = client.getCache("object_cache");

        // Put/Set/Push into cache
        cache.push(key, object);

        // Check if key exists in cache 
        boolean exists = cache.exists(key).sync();
        // Or do something with the CompletableFuture
        CompletableFuture<Boolean> future = cache.exists(key).future();

        // Remove from cache
        cache.invalidate(key);

        // Get from cache and sync the future
        TestObj objfromCache = cache.resolve(key).sync();
        // Or do something with the CompletableFuture
        CompletableFuture<TestObj> future = cache.resolve(key).future();

        // All methods are also available with keyword "Many"

        Map<String, TestObj> toCache = new HashMap<>();
        cache.pushMany(toCache);
        
        
        List<String> keyList = new ArrayList<>();
        
        cache.invalidateMany(keyList);
        
        Map<String, Boolean> existsMap = cache.existsMany(keyList).sync();
        CompletableFuture<Map<String, Boolean>> futureMap = cache.existsMany(keyList).future();
        
        Map<String, TestObj> resolveMap = cache.resolveMany(keyList).sync();
        CompletableFuture<Map<String, TestObj>> future = cache.resolveMany(keyList).future();
        
        // Special methods:

        Map<String, Object> resolveAllMap = cache.resolveAll().sync();
        CompletableFuture<Map<String, TestObj>> resolveAllMap = cache.resolveAll().future();
        cache.invalidateAll();
    }

}
````