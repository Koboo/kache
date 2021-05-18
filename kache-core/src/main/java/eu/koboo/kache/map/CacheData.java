package eu.koboo.kache.map;

public class CacheData {

    private final long lastModified;
    private final boolean forced;

    public CacheData(long lastModified, boolean forced) {
        this.lastModified = lastModified;
        this.forced = forced;
    }

    public long getLastModified() {
        return lastModified;
    }

    public boolean isForced() {
        return forced;
    }
}
