package utils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MovementUtils {

    public int getKey() {
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            System.out.println("Pressed key: Keyboard.KEY_DOWN: " + Keyboard.KEY_DOWN);
            return Keyboard.KEY_DOWN;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            System.out.println("Pressed key: Keyboard.KEY_UP: " + Keyboard.KEY_UP);
            return Keyboard.KEY_UP;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            System.out.println("Pressed key: Keyboard.KEY_LEFT: " + Keyboard.KEY_LEFT);
            return Keyboard.KEY_LEFT;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            System.out.println("Pressed key: Keyboard.KEY_RIGHT: " + Keyboard.KEY_RIGHT);
            return Keyboard.KEY_RIGHT;
        }

        return 0;
    }

    public static boolean isKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_UP)
                || Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
    }

    public static int getMouseDWheel() {
        return Mouse.getDWheel();
    }
}
