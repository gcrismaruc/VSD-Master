package com.crismaruc.main;

import com.crismaruc.entities.ProcessedObject;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessedObjectsQueue {
    ConcurrentLinkedQueue<ProcessedObject> processedObjects;

    public ProcessedObjectsQueue() {
        this.processedObjects = new ConcurrentLinkedQueue<>();
    }

    public ProcessedObjectsQueue(
            ConcurrentLinkedQueue<ProcessedObject> processedObjects) {
        this.processedObjects = processedObjects;
    }

    public void add(ProcessedObject processedObject) {
        processedObjects.add(processedObject);
    }

    public ProcessedObject get(){
        return processedObjects.poll();
    }
}
