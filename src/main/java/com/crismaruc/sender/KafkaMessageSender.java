package com.crismaruc.sender;

import com.crismaruc.entities.ProcessingFrame;
import com.crismaruc.entities.Scene;
import com.crismaruc.main.Master;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.crismaruc.utils.MovementUtils;

import java.time.Instant;

public class KafkaMessageSender implements Runnable {

    private Scene scene;
    KafkaProducer<String, ProcessingFrame> producer;

    public KafkaMessageSender(KafkaProducer<String, ProcessingFrame> producer) {
        this.producer = producer;
    }

    public void run() {
        Instant start = Instant.now();
        int key = MovementUtils.getKey();
        int dWheel = MovementUtils.getMouseDWheel();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scene.getFrames().forEach(frame -> {

            frame.setMouseWheel(dWheel);
            frame.setKeyboard(key);

            System.out.println("Sending frame: " + frame.getName());
            producer.send(new ProducerRecord<>(
                    Master.getProducerProperty().getProperty("kafka.topic"), frame));

        });

        scene.getFrames().forEach(frame -> {
            frame.setKeyboard(0);
        });
        //        System.out.println("Sending messages = " + Duration.between(start, Instant.now()).toMillis() + " ms");

    }

    public Scene getScene() {
        return scene;
    }

    public KafkaMessageSender setScene(Scene scene) {
        this.scene = scene;
        return this;
    }

    public KafkaProducer<String, ProcessingFrame> getProducer() {
        return producer;
    }

    public KafkaMessageSender setProducer(KafkaProducer<String, ProcessingFrame> producer) {
        this.producer = producer;
        return this;
    }
}
