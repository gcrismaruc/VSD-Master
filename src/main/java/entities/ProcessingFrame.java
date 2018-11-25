package entities;

import java.io.Serializable;

public class ProcessingFrame implements Serializable {
    private String name;
    private int keyboard;
    private int mouseWheel;

    private boolean isConsumed = false;

    public ProcessingFrame() {
    }

    public int getMouseWheel() {
        return mouseWheel;
    }

    public ProcessingFrame setMouseWheel(int mouseWheel) {
        this.mouseWheel = mouseWheel;
        return this;
    }

    public String getName() {
        return name;
    }

    public int getKeyboard() {
        return keyboard;
    }

    public ProcessingFrame setKeyboard(int keyboard) {
        this.keyboard = keyboard;
        return this;
    }

    public ProcessingFrame setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    public ProcessingFrame setConsumed(boolean consumed) {
        isConsumed = consumed;
        return this;
    }
}
