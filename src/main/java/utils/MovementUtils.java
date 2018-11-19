package utils;

import entities.Command;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MovementUtils {

    public static final int MOUSE_VELOCITY = 10;
    public static final float MOVEMENT_FACTOR = 0.9f;

    public static void moveObject(Command command) {
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            command.setMoveDown(true);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            command.setMoveUp(true);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            command.setMoveLeft(true);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            command.setMoveRight(true);
        }

        if (Mouse.isButtonDown(0)) {
            command.setMoveOnX(true);
            command.setMoveOnY(true);
        }

        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            command.setZoomIn(true);
        } else if (dWheel > 0) {
            command.setZoomOut(true);
        }
    }
}
