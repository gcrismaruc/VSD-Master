package main;

import entities.ProcessedObject;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.zip.GZIPInputStream;

public class DecompressingThread implements Runnable {

    private byte[] bytes;
    private ProcessedObjectsQueue processedObjectsQueue;
//    private ProcessedObject processedObject;



    public void run() {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        GZIPInputStream gis;
        Instant start = Instant.now();
        try {
            gis = new GZIPInputStream(bis);
            ObjectInputStream objectInputStream = new ObjectInputStream(gis);
            processedObjectsQueue.add((ProcessedObject)objectInputStream.readObject());

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Decompressing finished in: " + Duration.between(start, Instant.now()).toMillis() + " ms");
    }

    public byte[] getBytes() {
        return bytes;
    }

    public DecompressingThread setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public ProcessedObject getProcessedObject() {
        return processedObjectsQueue.get();
    }

    public ProcessedObjectsQueue getProcessedObjectsQueue() {
        return processedObjectsQueue;
    }

    public DecompressingThread setProcessedObjectsQueue(
            ProcessedObjectsQueue processedObjectsQueue) {
        this.processedObjectsQueue = processedObjectsQueue;
        return this;
    }

    //    public ProcessedObject getProcessedObject() {
//        System.out.println("Pixels are here: " + processedObject.getPixels().length);
//        return processedObject;
//    }
//
//    public main.DecompressingThread setProcessedObject(ProcessedObject processedObject) {
//        this.processedObject = processedObject;
//        return this;
//    }
}
