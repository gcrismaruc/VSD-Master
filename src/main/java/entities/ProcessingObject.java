package entities;

import java.io.Serializable;

public class ProcessingObject implements Serializable {
    private float rx;
    private float ry;
    private float rz;
    private float x;
    private float y;
    private float z;

    private float previousRx;
    private float previousRy;
    private float previousRz;
    private float previousX;
    private float previousY;
    private float previousZ;

    private boolean isConsumed = false;

    private String objectName;

    public ProcessingObject(float rx, float ry, float rz, float x, float y, float z,
            String objectName) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.x = x;
        this.y = y;
        this.z = z;
        this.objectName = objectName;
    }

    public float getPreviousRx() {
        return previousRx;
    }

    public ProcessingObject setPreviousRx(float previousRx) {
        this.previousRx = previousRx;
        return this;
    }

    public float getPreviousRy() {
        return previousRy;
    }

    public ProcessingObject setPreviousRy(float previousRy) {
        this.previousRy = previousRy;
        return this;
    }

    public float getPreviousRz() {
        return previousRz;
    }

    public ProcessingObject setPreviousRz(float previousRz) {
        this.previousRz = previousRz;
        return this;
    }

    public float getPreviousX() {
        return previousX;
    }

    public ProcessingObject setPreviousX(float previousX) {
        this.previousX = previousX;
        return this;
    }

    public float getPreviousY() {
        return previousY;
    }

    public ProcessingObject setPreviousY(float previousY) {
        this.previousY = previousY;
        return this;
    }

    public float getPreviousZ() {
        return previousZ;
    }

    public ProcessingObject setPreviousZ(float previousZ) {
        this.previousZ = previousZ;
        return this;
    }

    public ProcessingObject() {
    }

    public float getRx() {
        return rx;
    }

    public ProcessingObject setRx(float rx) {
        this.rx = rx;
        return this;
    }

    public float getRy() {
        return ry;
    }

    public ProcessingObject setRy(float ry) {
        this.ry = ry;
        return this;
    }

    public float getRz() {
        return rz;
    }

    public ProcessingObject setRz(float rz) {
        this.rz = rz;
        return this;
    }

    public float getX() {
        return x;
    }

    public ProcessingObject setX(float x) {
        this.x = x;
        return this;
    }

    public float getZ() {
        return z;
    }

    public ProcessingObject setZ(float z) {
        this.z = z;
        return this;
    }

    public float getY() {
        return y;
    }

    public ProcessingObject setY(float y) {
        this.y = y;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public ProcessingObject setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public void increaseRotationOnY() {
        this.previousRy = this.ry;
        if (this.ry == 259) {
            this.ry = 0;
        }
        this.ry += 1.0;
    }

    public void increaseRotationOnZ() {
        previousRz = rz;
        rz += 1.0;
    }

    public void increaseRotationOnX() {
        previousRx = rx;
        rx += 1.0;
    }

    public void moveOnX() {
        previousX = x;
        x += 1.0;
    }

    public void moveOnY() {
        previousY = y;
        y += 1.0;
    }

    public void moveOnZ() {
        previousZ = z;
        z += 1.0;
    }

    public void moveOnX(float factor) {
        previousX = x;
        x += factor;
    }

    public void moveOnY(float factor) {
        previousY = y;
        y += factor;
    }

    public void moveOnZ(float factor) {
        previousZ = z;
        z += factor;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    public ProcessingObject setConsumed(boolean consumed) {
        isConsumed = consumed;
        return this;
    }
}
