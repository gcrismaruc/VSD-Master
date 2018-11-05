import entities.ProcessedObject;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

public class DecompressingThread implements Runnable {

    private byte[] bytes;
    private ProcessedObject processedObject;



    public void run() {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        GZIPInputStream gis;
        try {
            gis = new GZIPInputStream(bis);
            ObjectInputStream objectInputStream = new ObjectInputStream(gis);
            processedObject = (ProcessedObject) objectInputStream.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public DecompressingThread setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public ProcessedObject getProcessedObject() {
        return processedObject;
    }

    public DecompressingThread setProcessedObject(ProcessedObject processedObject) {
        this.processedObject = processedObject;
        return this;
    }
}
