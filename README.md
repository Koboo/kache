# **Koboo's Cache** *(Kache)*

Kache stands for Koboo's Cache and is a minimalist and lightweight framework for remote or local transport operations. 
Kache is based on [Endpoint-Netty](https://github.com/Koboo/endpoint-netty), or rather a regular and conventional TCP client-server communication. 
<br>
**Features:**
* [Server-side cache](#use-sharedcacheobject)
* [Transfer Channels (Client-To-Server-To-Client)](#use-transferchannelobject)

Note: The transport encoding is based on the ``Protocol.NATIVE`` of [Endpoint-Netty](https://github.com/Koboo/endpoint-netty), 
but the object serialization is based on the framework [fast-serialization](https://github.com/RuedigerMoeller/fast-serialization), 
whereby objects have to extend the class ``Serializable``!

# Important

**It's recommend to use this framework only on localhost-running apps to share maps across JVM using `UnixDomainSocket`.**

# Usage
  
**All methods and functions are shown here as examples**  

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

### Use ``SharedCache<Object extends Transferable>``
````java
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SomeClass {

    public static void main(String[] args) {
        KacheClient client = new KacheClient();

        String key = "key"; // any key

        TransferObject transferObject = new TransferObject(); // any object, have to extend Transferable 
        transferObject.setTestString("SomeString");
        transferObject.setTestInt(1);
        transferObject.setTestLong(-1L);

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
        TransferObject objfromCache = cache.resolve(key).sync();
        // Or do something with the CompletableFuture
        CompletableFuture<TransferObject> future = cache.resolve(key).future();

        // All methods are also available with keyword "Many"

        Map<String, TransferObject> toCache = new HashMap<>();
        cache.pushMany(toCache);
        
        
        List<String> keyList = new ArrayList<>();
        
        cache.invalidateMany(keyList);
        
        Map<String, Boolean> existsMap = cache.existsMany(keyList).sync();
        CompletableFuture<Map<String, Boolean>> futureMap = cache.existsMany(keyList).future();
        
        Map<String, TransferObject> resolveMap = cache.resolveMany(keyList).sync();
        CompletableFuture<Map<String, TransferObject>> future = cache.resolveMany(keyList).future();
        
        // Special methods:

        Map<String, Object> resolveAllMap = cache.resolveAll().sync();
        CompletableFuture<Map<String, TransferObject>> resolveAllMap = cache.resolveAll().future();
        cache.invalidateAll();
    }

}
````

### Use ``TransferChannel<Object extends Transferable>``
````java
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class SomeClass {

    public static void main(String[] args) {
        KacheClient client = new KacheClient();
        
        TransferObject transferObject = new TransferObject(); // any object, have to extend Transferable 
        transferObject.setTestString("SomeString");
        transferObject.setTestInt(1);
        transferObject.setTestLong(-1L); 

        TransferChannel<TransferObject> channel = client.getTransfer("channelName"); // any Channel-Name

        // Publish an object to the clients, who registered the specific TransferChannel 
        channel.publish(object);
        
        // Get the name of the TransferChannel 
        String channelName = channel.getChannel();
        
        // Register a Consumer, which if fired by receiving an Object on the TransferChannel
        channel.receive(obj -> {
            // Do something with the TransferObject
        });
        
        // Stop receiving the Objets of the TransferChannel
        channel.pause(true);

        // Start receiving the Objets of the TransferChannel
        channel.pause(false);
        
    }

}
````

# Add as dependency 


Add `repo.koboo.eu` as repository.

```groovy
repositories {
    maven { 
        url 'https://repo.koboo.eu/releases' 
    }
}
```

Add a specific module as dependency. (e.g. `1.0` is the release-version)
```groovy
dependencies {
    // !Always needed!
    compile 'eu.koboo:kache-core:1.0'
    // Compile the client-side library
    compile 'eu.koboo:kache-client:1.0'
    // Compile the server-side library
    compile 'eu.koboo:kache-server:1.0'
}
```

Or you can build the ``kache-server`` by starting the task:
````bash
gradle kache-server:build
````

Output-Directory: ``{project}/kache-server/build/libs/kache.jar``