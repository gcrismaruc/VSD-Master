package entities;

import java.io.Serializable;

public class ProcessingFrame implements Serializable {
    private String name;
    private Command command;

    private boolean isConsumed = false;

    public ProcessingFrame() {
    }

    public String getName() {
        return name;
    }

    public ProcessingFrame setName(String name) {
        this.name = name;
        return this;
    }

    public Command getCommand() {
        return command;
    }

    public ProcessingFrame setCommand(Command command) {
        this.command = command;
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
