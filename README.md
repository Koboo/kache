# **Koboo's Cache** *(Kache)*

Kache stands for Koboo's Cache and is a minimalist and lightweight framework for remote or local operations. 
Kache is based on [Endpoint-Netty](https://github.com/Koboo/endpoint-netty), or rather a regular and conventional TCP client-server communication. 
<br>
**Features:**
* [Server-side cache](#use-sharedcacheobject)
* [Transfer Channels (Client-To-Server-To-Client)](#use-transferchannelobject)

Note: The transport encoding is based on the ``Protocol.NATIVE`` of [Endpoint-Netty](https://github.com/Koboo/endpoint-netty), 
but the object serialization is based on the framework [fast-serialization](https://github.com/RuedigerMoeller/fast-serialization), 
whereby objects in the cache have to extend the class ``Serializable``!

# Important

**It's recommend to use this framework only on localhost-running apps to share maps across JVM.**

# Usage

</br>
**All functions are shown here as examples**
</br>

### Create ``KacheClient``
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

### Use ``SharedCache<Object>``
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

### Use ``TransferChannel<Object>``
````java
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SomeClass {

    public static void main(String[] args) {
        KacheClient client = new KacheClient();

        TestObj object = new TestObj("", 1L, -1); // any object, have to extend Serializable 

        TransferChannel<TestObj> channel = client.getTransfer("channelName"); // any Channel-Name

        // Publish an object to the clients, who registered the specific TransferChannel 
        channel.publish(object);
        
        // Get the name of the TransferChannel 
        String channelName = channel.getChannel();
        
        // Register a Consumer, which if fired by receiving an Object on the TransferChannel
        channel.receive(obj -> {
            // Do something with the TestObj
        });
        
        // Stop receiving the Objets of the TransferChannel
        channel.pause(true);

        // Start receiving the Objets of the TransferChannel
        channel.pause(false);
        
    }

}
````