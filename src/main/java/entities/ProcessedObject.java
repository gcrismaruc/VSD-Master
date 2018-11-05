package entities;

import java.io.Serializable;
import java.util.Arrays;

public class ProcessedObject implements Serializable {
    private byte[] depthBuffer;
    private byte [] pixels;

    public ProcessedObject(byte [] depthBuffer, byte []pixels) {
        this.depthBuffer = depthBuffer;
        this.pixels = pixels;
    }

    public ProcessedObject() {
    }

    public byte[] getDepthBuffer() {
        return depthBuffer;
    }

    public ProcessedObject setDepthBuffer(byte[] depthBuffer) {
        this.depthBuffer = depthBuffer;
        return this;
    }

    public byte[] getPixels() {
        return pixels;
    }

    public ProcessedObject setPixels(byte[] pixels) {
        this.pixels = pixels;
        return this;
    }

    @Override
    public String toString() {
        return "entities.ProcessedObject{" +
                "depthBuffer=" + Arrays.toString(depthBuffer) +
                ", pixels=" + Arrays.toString(pixels) +
                '}';
    }
}
