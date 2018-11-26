package com.crismaruc.utils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MovementUtils {
    
    
    public static int getKey(){
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            return Keyboard.KEY_DOWN;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            return Keyboard.KEY_UP;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            return Keyboard.KEY_LEFT;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            return Keyboard.KEY_RIGHT;
        }

        return 0;
    }
    
    
    public static int getMouseDWheel() {
        return Mouse.getDWheel();
    }
}
