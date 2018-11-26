package com.crismaruc.main;

import com.crismaruc.entities.ProcessedObject;
import com.crismaruc.entities.ProcessingFrame;
import com.crismaruc.entities.Scene;
import com.crismaruc.sender.KafkaMessageSender;
import com.jogamp.opengl.GL;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private static final int FPS_CAP = 60;

    public static int SLAVES_NUMBER = 2;

    private static final float[] depths = new float[WIDTH * HEIGHT];

    public void start() {
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle("Master");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        ByteBuffer renderBuffer;

        // init OpenGL
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, HEIGHT, 0, WIDTH, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        ByteBuffer byteBuffer;
        boolean isFirstImage = true;

        Scene scene = new Scene();
        scene.setFrames(Arrays.asList(new ProcessingFrame().setName("frame1"),
                new ProcessingFrame().setName("frame2")));

        try {

            //Start receiving objects
            KafkaConsumer<String, ProcessedObject> consumer = new KafkaConsumer<String, ProcessedObject>(
                    getConsumerProperties());
            consumer.subscribe(Arrays.asList(getConsumerProperties().getProperty("kafka.topic")));

            //Start sending requests to slaves
            KafkaProducer<String, ProcessingFrame> producer = new KafkaProducer<String, ProcessingFrame>(
                    getProducerProperty());

            KafkaMessageSender messageSender = new KafkaMessageSender(producer);
            messageSender.setScene(scene);

            ExecutorService executorService = Executors.newFixedThreadPool(SLAVES_NUMBER + 1);

            executorService.execute(messageSender);

            List<ProcessedObject> processedObjects = new ArrayList<>();
            List<DecompressingThread> decompressingThreads = new ArrayList<>();

            Thread.sleep(3000L);

            while (!Display.isCloseRequested()) {

                // Clear the screen and depth buffer
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

                try {
                    System.out.println(
                            "***********************************************************************");

                    Instant startLoop = Instant.now();
                    Instant startReceiving = Instant.now();

                    ConsumerRecords<String, ProcessedObject> records = consumer.poll(Duration.ofMillis(2000));
                    while (records.isEmpty()) {
                        records = consumer.poll(Duration.ofMillis(3000));
                    }
                    records.forEach(record -> {
                        processedObjects.add(record.value());
                    });
                    //wait the decompressing threads to finish their job
//                    executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
                    System.out.println(
                            "Receiving time = " + Duration.between(startReceiving, Instant.now())
                                    .toMillis() + " ms");

                    if (isFirstImage) {
                        byteBuffer = ByteBuffer
                                .allocateDirect(processedObjects.get(0).getPixels().length);
                        byteBuffer.put(processedObjects.get(0).getPixels());
                        byteBuffer.rewind();
                        GL11.glDrawPixels(WIDTH, HEIGHT, GL.GL_RGB, GL11.GL_UNSIGNED_BYTE,
                                byteBuffer);
                        Display.update();
                        isFirstImage = false;
                    } else {

                        Instant start = Instant.now();
                        renderBuffer = updateBuffer(processedObjects);

                        System.out.println(
                                "Merging time = " + Duration.between(start, Instant.now())
                                        .toMillis() + " ms");

                        GL11.glDrawPixels(WIDTH, HEIGHT, GL.GL_RGB, GL11.GL_UNSIGNED_BYTE,
                                renderBuffer);
                        Display.update();
                    }

                    executorService.execute(messageSender);

//                    decompressingThreads.removeAll(decompressingThreads);
                    processedObjects.removeAll(processedObjects);

                    System.out.println(
                            "Total loop time: " + Duration.between(startLoop, Instant.now())
                                    .toMillis() + " ms");
                    System.out.println(
                            "----------------------------------------------------------------------------------");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception exp) {
            System.out.println("Caught exception, exiting.");
            exp.printStackTrace(System.out);
            System.exit(1);
        }

        Display.destroy();
    }

    private static ByteBuffer updateBuffer(List<ProcessedObject> processedObjects) {
        int buffersSize = processedObjects.get(0).getPixels().length;

        ByteBuffer bufferToReturn = ByteBuffer.allocateDirect(buffersSize);

        int listSize = processedObjects.size();

        if (listSize == 2) {
            bufferToReturn
                    .put(getImage(processedObjects.get(0), processedObjects.get(1).getPixels(),
                            processedObjects.get(1).getDepthBuffer()).getPixels());
        } else {
            if (listSize > 2) {
                ProcessedObject processedObject = getImage(processedObjects.get(0),
                        processedObjects.get(1).getPixels(),
                        processedObjects.get(1).getDepthBuffer());
                for (int i = 2; i < listSize; i++) {
                    processedObject = getImage(processedObject, processedObjects.get(i).getPixels(),
                            processedObjects.get(i).getDepthBuffer());
                }
                bufferToReturn.put(processedObject.getPixels());
            }
        }

        bufferToReturn.rewind();
        return bufferToReturn;
    }

    private static ProcessedObject getImage(ProcessedObject processedObject, byte[] pixelsBuffer,
            byte[] depthBuffer) {

        byte[] newPixels = new byte[processedObject.getPixels().length];
        byte[] newDepths = new byte[processedObject.getDepthBuffer().length];

        int pixelIndex = 0;
        for (int i = 0; i < processedObject.getDepthBuffer().length; i += 4) {
            if (depthBuffer[i + 2] > processedObject.getDepthBuffer()[i + 2]) {
                newPixels[pixelIndex] = pixelsBuffer[pixelIndex];
                newPixels[pixelIndex + 1] = pixelsBuffer[pixelIndex + 1];
                newPixels[pixelIndex + 2] = pixelsBuffer[pixelIndex + 2];
                newDepths[i] = depthBuffer[i + 2];
            } else {
                newPixels[pixelIndex] = processedObject.getPixels()[pixelIndex];
                newPixels[pixelIndex + 1] = processedObject.getPixels()[pixelIndex + 1];
                newPixels[pixelIndex + 2] = processedObject.getPixels()[pixelIndex + 2];
                newDepths[i] = processedObject.getDepthBuffer()[i + 2];
            }
            pixelIndex += 3;
        }

        return processedObject.setPixels(newPixels).setDepthBuffer(newDepths);
    }

    public static void main(String[] argv) {
        Master quadExample = new Master();
        quadExample.start();
    }

    private static class MyExceptionListener implements ExceptionListener {
        public void onException(JMSException exception) {
            System.out.println("Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }

    public static Properties getProducerProperty() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("acks", "0");
        properties.put("retries", "1");
        properties.put("batch.size", "20971520");
        properties.put("linger.ms", "33");
        properties.put("max.request.size", "2097152");
        properties.put("compression.type", "gzip");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", com.crismaruc.entities.ProcessingFrameSerializer.class);
        properties.put("kafka.topic", "slave-commands");

        return properties;
    }

    public static Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("kafka.topic", "slave-output");
        properties.put("compression.type", "gzip");
        properties.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer",
                com.crismaruc.entities.ProcessedObjectDeserializer.class);
        properties.put("max.partition.fetch.bytes", "209715200");
        properties.put("max.poll.records", SLAVES_NUMBER);
        properties.put("group.id", "my-group");

        return properties;
    }

}