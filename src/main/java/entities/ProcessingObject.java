package entities;

import java.io.Serializable;

public class ProcessingObject implements Serializable {
    private int rx;
    private int ry;
    private int rz;
    private int x;
    private int y;
    private int z;

    private int previousRx;
    private int previousRy;
    private int previousRz;
    private int previousX;
    private int previousY;
    private int previousZ;

    private float pitch;
    private float yaw;
    private String objectName;

    public ProcessingObject(int rx, int ry, int rz, int x, int y, int z, float pitch, float yaw,
            String objectName) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.objectName = objectName;
    }

    public int getPreviousRx() {
        return previousRx;
    }

    public ProcessingObject setPreviousRx(int previousRx) {
        this.previousRx = previousRx;
        return this;
    }

    public int getPreviousRy() {
        return previousRy;
    }

    public ProcessingObject setPreviousRy(int previousRy) {
        this.previousRy = previousRy;
        return this;
    }

    public int getPreviousRz() {
        return previousRz;
    }

    public ProcessingObject setPreviousRz(int previousRz) {
        this.previousRz = previousRz;
        return this;
    }

    public int getPreviousX() {
        return previousX;
    }

    public ProcessingObject setPreviousX(int previousX) {
        this.previousX = previousX;
        return this;
    }

    public int getPreviousY() {
        return previousY;
    }

    public ProcessingObject setPreviousY(int previousY) {
        this.previousY = previousY;
        return this;
    }

    public int getPreviousZ() {
        return previousZ;
    }

    public ProcessingObject setPreviousZ(int previousZ) {
        this.previousZ = previousZ;
        return this;
    }

    public ProcessingObject() {
    }

    public float getPitch() {
        return pitch;
    }

    public ProcessingObject setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public float getYaw() {
        return yaw;
    }

    public ProcessingObject setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public int getRx() {
        return rx;
    }

    public ProcessingObject setRx(int rx) {
        this.rx = rx;
        return this;
    }

    public int getRy() {
        return ry;
    }

    public ProcessingObject setRy(int ry) {
        this.ry = ry;
        return this;
    }

    public int getRz() {
        return rz;
    }

    public ProcessingObject setRz(int rz) {
        this.rz = rz;
        return this;
    }

    public int getX() {
        return x;
    }

    public ProcessingObject setX(int x) {
        this.x = x;
        return this;
    }

    public int getZ() {
        return z;
    }

    public ProcessingObject setZ(int z) {
        this.z = z;
        return this;
    }

    public int getY() {
        return y;
    }

    public ProcessingObject setY(int y) {
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
        previousRy = ry;
        ry += 1.0;
    }
    public void increaseRotationOnZ() {
        previousRz = rz;
        rz += 1.0;
    }
    public void increaseRotationOnX() {
        previousRx = rx;
        rx += 1.0;
    }

    public void moveOnX(){
        previousX = x;
        x += 1.0;
    }
    public void moveOnY(){
        previousY = y;
        y += 1.0;
    }
    public void moveOnZ(){
        previousZ = z;
        z += 1.0;
    }

}
