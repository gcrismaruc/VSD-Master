package utils;

import entities.ProcessingObject;
import org.lwjgl.input.Mouse;

public class MovementUtils {

    public static final int MOUSE_VELOCITY = 10;
    public static final float MOVEMENT_FACTOR = 0.9f;

    public static void moveObject(ProcessingObject processingObject) {
//        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
//            processingObject.movePitch(-MOVEMENT_FACTOR);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
//            processingObject.movePitch(MOVEMENT_FACTOR);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
//            processingObject.moveYaw(MOVEMENT_FACTOR);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
//            processingObject.moveYaw(-MOVEMENT_FACTOR);
//        }

        if (Mouse.isButtonDown(0)) {
            processingObject.moveOnX(-Mouse.getDX() * 0.05f);
            processingObject.moveOnY(-Mouse.getDY() * 0.05f);
        }

        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            processingObject.moveOnZ(MOUSE_VELOCITY);
        } else if (dWheel > 0) {
            processingObject.moveOnZ(-MOUSE_VELOCITY);
        }
    }
}
