
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

public class TransferObject implements Serializable {

    private String testString;
    private int testInt;
    private long testLong;

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
