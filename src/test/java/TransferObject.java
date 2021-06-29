import eu.koboo.endpoint.transferable.Transferable;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TransferObject implements Transferable {

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

    @Override
    public void readStream(DataInputStream dataInputStream) throws Exception {
        setTestString(dataInputStream.readUTF());
        setTestInt(dataInputStream.readInt());
        setTestLong(dataInputStream.readLong());
    }

    @Override
    public void writeStream(DataOutputStream dataOutputStream) throws Exception {
        dataOutputStream.writeUTF(getTestString());
        dataOutputStream.writeInt(getTestInt());
        dataOutputStream.writeLong(getTestLong());
    }
}
