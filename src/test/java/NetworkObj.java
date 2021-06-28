import java.io.Serializable;

public class NetworkObj implements Serializable {

    private String testString;
    private int testInt;
    private long testLong;

    public NetworkObj(String testString, int testInt, long testLong) {
        this.testString = testString;
        this.testInt = testInt;
        this.testLong = testLong;
    }

    public String getTestString() {
        return testString;
    }

    public int getTestInt() {
        return testInt;
    }

    public long getTestLong() {
        return testLong;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }

    public void setTestLong(long testLong) {
        this.testLong = testLong;
    }

}
