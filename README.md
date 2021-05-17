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

public class SomeClass {

    public static void main(String[] args) {
        KacheClient client = new KacheClient();

        String key = "key"; // any key
        Object object = new Object(); // any object 

        LocalCache<Object> cache = client.getCache("object_cache");

        // Put into cache
        cache.push(key, object);

        // Check if key exists in cache 
        boolean exists = cache.exists(key);

        // Remove from cache
        cache.invalidate(key);

        // Get from cache
        object = cache.resolve(key);

        // All methods are also available with keyword "Many"

        Map<String, Object> toCache = new HashMap<>();
        List<String> keyList = new ArrayList<>();

        cache.pushMany(toCache);
        cache.invalidateMany(keyList);
        Map<String, Boolean> existsMap = cache.existsMany(keyList);
        Map<String, Object> resolveMap = cache.resolveMany(keyList); 

        // Special methods:
        
        Map<String, Object> resolveAllMap = cache.resolveAll();
        cache.invalidateAll();
    }

}
````