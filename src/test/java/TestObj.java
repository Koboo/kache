import java.io.Serializable;

public class TestObj implements Serializable {

    private final String string;
    private final int inte;
    private final long lon;

    public TestObj(String string, int inte, long lon) {
        this.string = string;
        this.inte = inte;
        this.lon = lon;
    }

    public String getString() {
        return string;
    }

    public int getInte() {
        return inte;
    }

    public long getLon() {
        return lon;
    }
}
