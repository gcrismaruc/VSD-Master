package com.crismaruc.entities;

import java.util.List;

public class Scene {
    private List<ProcessingFrame> frames;

    public Scene() {
    }

    public List<ProcessingFrame> getFrames() {
        return frames;
    }

    public Scene setFrames(List<ProcessingFrame> frames) {
        this.frames = frames;
        return this;
    }
}
