package entities;

import java.io.Serializable;

public class Command implements Serializable {
    boolean moveRight;
    boolean moveLeft;
    boolean moveUp;
    boolean moveDown;
    boolean moveOnX;
    boolean moveOnY;
    boolean zoomIn;
    boolean zoomOut;

    public Command() {
    }

    public boolean isZoomOut() {
        return zoomOut;
    }

    public Command setZoomOut(boolean zoomOut) {
        this.zoomOut = zoomOut;
        return this;
    }

    public boolean isMoveLeft() {
        return moveLeft;
    }

    public Command setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
        return this;
    }

    public boolean isMoveUp() {
        return moveUp;
    }

    public Command setMoveUp(boolean moveUp) {
        this.moveUp = moveUp;
        return this;
    }

    public boolean isMoveDown() {
        return moveDown;
    }

    public Command setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
        return this;
    }

    public boolean isMoveOnX() {
        return moveOnX;
    }

    public Command setMoveOnX(boolean moveOnX) {
        this.moveOnX = moveOnX;
        return this;
    }

    public boolean isMoveOnY() {
        return moveOnY;
    }

    public Command setMoveOnY(boolean moveOnY) {
        this.moveOnY = moveOnY;
        return this;
    }

    public boolean isZoomIn() {
        return zoomIn;
    }

    public Command setZoomIn(boolean zoomIn) {
        this.zoomIn = zoomIn;
        return this;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public Command setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
        return this;
    }
}
